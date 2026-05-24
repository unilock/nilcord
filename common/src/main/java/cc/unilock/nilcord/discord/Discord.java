package cc.unilock.nilcord.discord;

import cc.unilock.nilcord.Constants;
import cc.unilock.nilcord.Nilcord;
import cc.unilock.nilcord.util.TextUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.unilock.nilcord.Nilcord.CONFIG;

public class Discord extends ListenerAdapter {
    private static final Pattern WEBHOOK_ID_REGEX = Pattern.compile("^https://discord\\.com/api/webhooks/(\\d+)/.+$");

    private final JDA jda;
    private final IncomingWebhookClient webhook;
    private final String webhookId;

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

        if (CONFIG.discord.webhook.enabled.value()) {
            try {
                this.webhook = WebhookClient.createClient(jda, CONFIG.discord.webhook.url.value());
                Matcher matcher = WEBHOOK_ID_REGEX.matcher(CONFIG.discord.webhook.url.value());
                this.webhookId = matcher.find() ? matcher.group(1) : null;
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid webhook URL!");
            }
        } else {
            this.webhook = null;
            this.webhookId = null;
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
        if (!event.getChannel().asTextChannel().getId().equals(CONFIG.discord.channel_id.value())) return;

        if (!CONFIG.minecraft.show_webhook_messages.value() && event.isWebhookMessage()) return;

        User author = event.getAuthor();
        if (!CONFIG.minecraft.show_bot_messages.value() && author.isBot()) return;
        if (CONFIG.minecraft.ignored_ids.value().contains(author.getId())) return;
        if (author.getId().equals(this.jda.getSelfUser().getId()) || author.getId().equals(this.webhookId)) return;

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
                CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value() : CONFIG.formatting.discord.chat_message.value(),
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
        if (!CONFIG.discord.webhook.enabled.value() || this.webhook == null || player == null) {
            sendBotMessageToDiscord(message);
        } else {
            sendWebhookMessageToDiscord(message, player);
        }
    }

    public void sendBotMessageToDiscord(String message) {
        TextChannel textChannel = this.jda.getTextChannelById(CONFIG.discord.channel_id.value());
        if (textChannel != null) {
            textChannel.sendMessage(message).queue();
        } else {
            Constants.LOG.error("Unable to find channel {}!", CONFIG.discord.channel_id.value());
        }
    }

    public void sendWebhookMessageToDiscord(String message, ServerPlayer player) {
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
    }

    private static final Pattern EVERYONE_AND_HERE_PATTERN = Pattern.compile("@(?<ping>everyone|here)");
    private String parseEveryoneAndHere(String message) {
        return EVERYONE_AND_HERE_PATTERN.matcher(message).replaceAll("@\u200B${ping}");
    }

    private String parseMentions(String message) {
        String msg = message;

        TextChannel textChannel = jda.getTextChannelById(CONFIG.discord.channel_id.value());
        if (textChannel != null) {
            for (Member member : textChannel.getMembers()) {
                msg = Pattern.compile(Pattern.quote("@" + member.getUser().getName()), Pattern.CASE_INSENSITIVE).matcher(msg).replaceAll(member.getAsMention());
            }
        }

        return msg;
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
