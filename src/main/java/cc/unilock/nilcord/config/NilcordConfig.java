package cc.unilock.nilcord.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class NilcordConfig extends ReflectiveConfig {
    @Comment("Settings pertaining to Discord itself")
    public final Discord discord = new Discord();
    public static final class Discord extends Section {
        @Comment("The Discord bot token to use")
        public final TrackedValue<String> token = value("EMPTY");

        @Comment("The Discord channel ID for the bot to send messages to / receive messages from")
        public final TrackedValue<String> channel_id = value("EMPTY");

        @Comment("Settings pertaining to the Discord webhook")
        public final Webhook webhook = new Webhook();
        public static final class Webhook extends Section {
            @Comment("Whether to use a webhook for sending players' chat messages to Discord")
            public final TrackedValue<Boolean> enabled = value(Boolean.FALSE);

            @Comment("The webhook URL to use")
            public final TrackedValue<String> url = value("EMPTY");
        }
    }

    @Comment("Settings pertaining to Minecraft itself")
    public final Minecraft minecraft = new Minecraft();
    public static final class Minecraft extends Section {
        @Comment("Whether to allow mentioning Discord users from Minecraft")
        public final TrackedValue<Boolean> enable_mentions = value(Boolean.FALSE);

        @Comment("Whether to allow mentioning @everyone and @here from Minecraft")
        public final TrackedValue<Boolean> enable_everyone_and_here = value(Boolean.FALSE);

        @Comment("Whether to show Discord message attachments in-game")
        public final TrackedValue<Boolean> show_attachments = value(Boolean.TRUE);

        @Comment("Whether to show messages from other Discord bots in-game")
        public final TrackedValue<Boolean> show_bot_messages = value(Boolean.FALSE);
    }

    @Comment("Settings pertaining to message formatting")
    public final Formatting formatting = new Formatting();
    public static final class Formatting extends Section {
        @Comment("Settings pertaining to messages visible in Discord")
        @Comment("Available placeholders: <username>")
        public final DiscordFormatting discord = new DiscordFormatting();
        public static final class DiscordFormatting extends Section {
            @Comment("Server start message")
            @Comment("Available placeholders: N/A")
            public final TrackedValue<String> server_start_message = value("**Server started!**");

            @Comment("Server stop message")
            @Comment("Available placeholders: N/A")
            public final TrackedValue<String> server_stop_message = value("**Server stopped!**");

            @Comment("Player chat messages")
            @Comment("Additional placeholders: <message>")
            public final TrackedValue<String> chat_message = value("**<username>** <message>");

            @Comment("Player join messages")
            @Comment("Additional placeholders: N/A")
            public final TrackedValue<String> join_message = value("> **<username> joined the game**");

            @Comment("Player leave messages")
            @Comment("Additional placeholders: N/A")
            public final TrackedValue<String> leave_message = value("> **<username> left the game**");

            @Comment("Player achievement messages")
            @Comment("Additional placeholders: <achievement_description> <achievement_title>")
            public final TrackedValue<String> achievement_message = value("> **<username>** has just earned the achievement **[<achievement_title>]**\n> \\> _<achievement_description>_");

            @Comment("Player death messages")
            @Comment("Additional placeholders: <death_message>")
            public final TrackedValue<String> death_message = value("**<username> died:** _<death_message>_");

            @Comment("Settings pertaining to messages sent from the webhook, if enabled")
            public final WebhookFormatting webhook = new WebhookFormatting();
            public static final class WebhookFormatting extends Section {
                @Comment("The URL to use for the webhook's avatar")
                @Comment("Additional placeholders: N/A")
                public final TrackedValue<String> avatar_url = value("https://visage.surgeplay.com/bust/128/<username>");

                @Comment("The webhook's username")
                @Comment("Additional placeholders: N/A")
                public final TrackedValue<String> username = value("<username>");

                @Comment("Player chat messages")
                @Comment("Additional placeholders: <message>")
                public final TrackedValue<String> chat_message = value("<message>");
            }
        }

        @Comment("Settings pertaining to messages visible in Minecraft")
        @Comment("Available placeholders: <message> <nickname> <username>")
        public final MinecraftFormatting minecraft = new MinecraftFormatting();
        public static final class MinecraftFormatting extends Section {
            @Comment("Discord messages")
            @Comment("Additional placeholders: <attachment_format> <discord_format> <reply_format> <username_format>")
            public final TrackedValue<String> discord_message = value("[Discord] <reply_format><username_format> <message><attachment_format>");

            @Comment("Username format")
            @Comment("Additional placeholders: N/A")
            public final TrackedValue<String> username_format = value("<<nickname>>");

            @Comment("Mention format")
            @Comment("Additional placeholders: N/A")
            public final TrackedValue<String> mention_format = value("§n@<nickname>§r");

            @Comment("Reply format")
            @Comment("Additional placeholders: <reply_message> <reply_nickname> <reply_url> <reply_username>")
            public final TrackedValue<String> reply_format = value("[§b←§r<reply_nickname] ");

            @Comment("Attachment format")
            @Comment("Additional placeholders: <attachment_url>")
            public final TrackedValue<String> attachment_format = value("[ §bAttachment: §n<attachment_url>§r ]");
        }
    }
}
