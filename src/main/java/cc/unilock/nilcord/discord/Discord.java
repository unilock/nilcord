package cc.unilock.nilcord.discord;

import cc.unilock.nilcord.util.TextUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.IncomingWebhookClient;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.unilock.nilcord.NilcordPremain.*;

public class Discord extends ListenerAdapter {
    private static final Pattern WEBHOOK_ID_REGEX = Pattern.compile("^https://discord\\.com/api/webhooks/(\\d+)/.+$");

    private final JDA jda;
    private final IncomingWebhookClient webhook;
    private final String webhookId;

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
        LOGGER.info("Bot ready!");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromType(ChannelType.TEXT)) return;
        if (!event.getChannel().asTextChannel().getId().equals(CONFIG.discord.channel_id.value())) return;

        User author = event.getAuthor();
        if (!CONFIG.minecraft.show_bot_messages.value() && author.isBot()) return;
        if (author.getId().equals(this.jda.getSelfUser().getId()) || author.getId().equals(this.webhookId)) return;

        Message message = event.getMessage();
        MessageReference ref = message.getMessageReference();

        Member member = message.getMember();
        if (member == null) return;

        StringBuilder attachment_chunk = new StringBuilder(message.getContentDisplay().isEmpty() ? "" : " ");
        if (CONFIG.minecraft.show_attachments.value()) {
            for (Message.Attachment attachment : message.getAttachments()) {
                attachment_chunk.append(CONFIG.formatting.minecraft.attachment_format.value().replace("<attachment_url>", attachment.getUrl()));
            }
        }

        Text reply_chunk = Text.empty();
        if (ref != null) {
            reply_chunk = TextUtils.parseDiscordReply(
                    CONFIG.formatting.minecraft.reply_format.value(),
                    ref.getMessage() == null ? ref.resolve().complete() : ref.getMessage()
            );
        }

        Text msg = TextUtils.parseDiscordMessage(
                CONFIG.formatting.minecraft.discord_message.value(),
                attachment_chunk.toString(),
                reply_chunk,
                CONFIG.formatting.minecraft.username_format.value(),
                author.getName(),
                member.getEffectiveName(),
                message.getContentDisplay()
        );

        server.sendMessage(msg);
        server.getPlayerManager().broadcast(msg, false);
    }

    public void onPlayerChatMessage(ServerPlayerEntity player, Text message) {
        String msg = TextUtils.parseMessage(
                CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value() : CONFIG.formatting.discord.chat_message.value(),
                player,
                message
        ).getString();

        if (CONFIG.minecraft.enable_everyone_and_here.value()) {
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

    public void sendMessageToDiscord(String message, @Nullable ServerPlayerEntity player) {
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
            LOGGER.error("Unable to find channel "+CONFIG.discord.channel_id.value()+"!");
        }
    }

    public void sendWebhookMessageToDiscord(String message, ServerPlayerEntity player) {
        String avatar = TextUtils.parsePlayer(
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

        for (Member member : jda.getTextChannelById(CONFIG.discord.channel_id.value()).getMembers()) {
            message = Pattern.compile(Pattern.quote("@" + member.getUser().getName()), Pattern.CASE_INSENSITIVE).matcher(msg).replaceAll(member.getAsMention());
        }

        return message;
    }

    public JDA getJda() {
        return this.jda;
    }

    public void shutdown() {
        this.jda.removeEventListener(this);
        this.jda.shutdown();
    }
}
