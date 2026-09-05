package cc.unilock.nilcord.discord;

import cc.unilock.nilcord.Constants;
import cc.unilock.nilcord.Nilcord;
import cc.unilock.nilcord.util.TextUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static cc.unilock.nilcord.Nilcord.CONFIG;

public class Discord extends ListenerAdapter {
    private final JDA jda;
    private final Map<String, WebhookClient<Message>> webhooks = new HashMap<>();
    private final Set<String> webhookIds = new HashSet<>();

    private boolean shutdown = false;

    public Discord() {
        JDABuilder builder = JDABuilder.createDefault(CONFIG.discord.token.value())
                .addEventListeners(this)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);

        try {
            this.jda = builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to log into Discord!", e);
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Constants.LOG.info("Bot ready!");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (Nilcord.server == null) return;

        if (CONFIG.formatting.minecraft.discord_message.value().isBlank()) return;

        if (!event.isFromType(ChannelType.TEXT)) return;
        if (!Nilcord.receiveChannels.contains(event.getChannel().asTextChannel().getId())) return;

        if (!CONFIG.minecraft.show_webhook_messages.value() && event.isWebhookMessage()) return;

        User author = event.getAuthor();
        if (!CONFIG.minecraft.show_bot_messages.value() && author.isBot()) return;
        if (CONFIG.minecraft.ignored_ids.value().contains(author.getId())) return;
        if (author.getId().equals(this.jda.getSelfUser().getId()) || webhookIds.contains(author.getId())) return;

        Message message = event.getMessage();
        MessageReference ref = message.getMessageReference();

        Member member = message.getMember();

        StringBuilder attachment_chunk = new StringBuilder(message.getContentDisplay().isEmpty() ? "" : " ");
        if (CONFIG.minecraft.show_attachments.value()) {
            for (Message.Attachment attachment : message.getAttachments()) {
                attachment_chunk.append(CONFIG.formatting.minecraft.attachment_format.value().replace("<attachment_url>", attachment.getUrl()));
            }
        }

        Component reply_chunk = Component.empty();
        if (ref != null) {
            reply_chunk = TextUtils.parseDiscordReply(
                    CONFIG.formatting.minecraft.reply_format.value(),
                    ref.getMessage() == null ? ref.resolve().complete() : ref.getMessage()
            );
        }

        Component msg = TextUtils.parseDiscordMessage(
                CONFIG.formatting.minecraft.discord_message.value(),
                attachment_chunk.toString(),
                reply_chunk,
                CONFIG.formatting.minecraft.username_format.value(),
                author,
                member,
                message
        );

        Nilcord.server.getPlayerList().broadcastSystemMessage(msg, false);
    }

    public void onPlayerChatMessage(ServerPlayer player, Component message) {
        String msg = TextUtils.parseMessage(
                CONFIG.discord.webhooks.value() ? CONFIG.formatting.discord.webhook.chat_message.value() : CONFIG.formatting.discord.chat_message.value(),
                player,
                message
        ).getString();

        if (!CONFIG.minecraft.enable_everyone_and_here.value()) {
            msg = parseEveryoneAndHere(msg);
        }
        if (CONFIG.minecraft.enable_mentions.value()) {
            msg = parseMentions(msg);
        }

        sendMessageToDiscord(msg, player);
    }

    public void sendMessageToDiscord(String message) {
        this.sendMessageToDiscord(message, null);
    }

    public void sendMessageToDiscord(String message, @Nullable ServerPlayer player) {
        if (this.shutdown) {
			Constants.LOG.error("Dropping message \"{}\" due to JDA shutdown!", message);
            return;
        }

        for (String channel : Nilcord.sendChannels) {
            if (!CONFIG.discord.webhooks.value() || player == null) {
                sendBotMessageToDiscord(channel, message);
            } else {
                sendWebhookMessageToDiscord(channel, message, player);
            }
        }
    }

    public void sendBotMessageToDiscord(String channel_id, String message) {
        TextChannel textChannel = this.jda.getTextChannelById(channel_id);
        if (textChannel != null) {
            textChannel.sendMessage(message).queue();
        } else {
            Constants.LOG.error("Unable to find channel {}!", channel_id);
        }
    }

    public void sendWebhookMessageToDiscord(String channel_id, String message, ServerPlayer player) {
        WebhookClient<Message> webhook = this.webhooks.get(channel_id);
        if (webhook != null) {
            String avatar = TextUtils.parseAvatar(
                    CONFIG.formatting.discord.webhook.avatar_url.value(),
                    player
            ).getString();

            String username = TextUtils.parsePlayer(
                    CONFIG.formatting.discord.webhook.username.value(),
                    player
            ).getString();

            try (MessageCreateData data = new MessageCreateBuilder().setContent(message).build()) {
                webhook.sendMessage(data)
                        .setAvatarUrl(avatar)
                        .setUsername(username)
                        .queue();
            }
        } else {
            Constants.LOG.error("Unable to find webhook for channel {}!", channel_id);
        }
    }

    private static final Pattern EVERYONE_AND_HERE_PATTERN = Pattern.compile("@(?<ping>everyone|here)");
    private String parseEveryoneAndHere(String message) {
        return EVERYONE_AND_HERE_PATTERN.matcher(message).replaceAll("@\u200B${ping}");
    }

    private String parseMentions(String message) {
        String msg = message;

        for (String channel : Nilcord.sendChannels) {
            TextChannel textChannel = this.jda.getTextChannelById(channel);
            if (textChannel != null) {
                for (Member member : textChannel.getMembers()) {
                    msg = Pattern.compile(Pattern.quote("@" + member.getUser().getName()), Pattern.CASE_INSENSITIVE).matcher(msg).replaceAll(member.getAsMention());
                }
            } else {
                Constants.LOG.error("Unable to find channel {}!", channel);
            }
        }

        return msg;
    }

    public void createWebhooks() {
        if (CONFIG.discord.webhooks.value()) {
            for (String channel : Nilcord.sendChannels) {
                TextChannel textChannel = this.jda.getTextChannelById(channel);
                if (textChannel != null) {
                    String name = CONFIG.id.value()+"_"+channel;
                    // TODO: async?
                    for (Webhook webhook : textChannel.retrieveWebhooks().complete()) {
                        if (name.equals(webhook.getName())) {
                            this.webhooks.put(channel, WebhookClient.createClient(this.jda, webhook.getUrl()));
                        }
                    }
                    this.webhooks.computeIfAbsent(channel, _ -> {
                        // TODO: async?
                        return WebhookClient.createClient(this.jda, textChannel.createWebhook(name).setAvatar(Icon.from(Constants.ICON, Icon.IconType.PNG)).complete().getUrl());
                    });
                } else {
                    Constants.LOG.error("Unable to find channel {}!", channel);
                }
            }

            for (WebhookClient<Message> webhook : webhooks.values()) {
                webhookIds.add(webhook.getId());
            }
        }
    }

    public void startJda() {
        try {
            this.jda.awaitReady();
        } catch (InterruptedException e) {
            Constants.LOG.error(e.toString());
        }
    }

    public void stopJda() {
        if (this.shutdown) return;
        this.shutdown = true;

        try {
            this.jda.removeEventListener(this);
            this.jda.shutdown();
            if (!this.jda.awaitShutdown(Duration.ofSeconds(3))) {
                Constants.LOG.error("JDA shutdown timeout exceeded! Shutting down now...");
                this.jda.shutdownNow();
            }
        } catch (InterruptedException e) {
            Constants.LOG.error(e.toString());
        }
    }
}
