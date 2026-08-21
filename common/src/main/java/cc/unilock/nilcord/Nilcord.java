package cc.unilock.nilcord;

import cc.unilock.nilcord.config.NilcordConfig;
import cc.unilock.nilcord.discord.Discord;
import cc.unilock.nilcord.platform.Services;
import cc.unilock.nilcord.util.TextUtils;
import folk.sisby.kaleido.lib.quiltconfig.api.Config;
import net.minecraft.CrashReport;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.gamerules.GameRules;

import java.util.HashSet;
import java.util.Set;

public class Nilcord {
    public static final NilcordConfig CONFIG = NilcordConfig.createToml(Services.PLATFORM.getConfigDir(), "", Constants.MOD_ID, NilcordConfig.class);

    public static Set<String> sendChannels = new HashSet<>();
    public static Set<String> receiveChannels = new HashSet<>();

    public static Discord discord;
    public static MinecraftServer server;

    private static boolean crash = false;

    public static void init() {
        refreshChannels(CONFIG);
        CONFIG.registerCallback(Nilcord::refreshChannels);

        discord = new Discord();
    }

    private static void refreshChannels(Config config) {
        if (config instanceof NilcordConfig nilcordConfig) {
            sendChannels.clear();
            receiveChannels.clear();

            var channelId = nilcordConfig.discord.channel_id.value();
            if (!channelId.isBlank()) {
                sendChannels.add(channelId);
                receiveChannels.add(channelId);
            }

            sendChannels.addAll(nilcordConfig.discord.send_to.value());
            receiveChannels.addAll(nilcordConfig.discord.receive_from.value());
        }
    }

    public static void serverStarting() {
        discord.startJda();

        if (!CONFIG.formatting.discord.server_starting_message.value().isBlank()) {
            discord.sendMessageToDiscord(CONFIG.formatting.discord.server_starting_message.value());
        }
    }

    public static void serverStarted() {
        if (!CONFIG.formatting.discord.server_started_message.value().isBlank()) {
            discord.sendMessageToDiscord(CONFIG.formatting.discord.server_started_message.value());
        }
    }

    public static void serverStopping() {
        if (crash) return;

        if (!CONFIG.formatting.discord.server_stopping_message.value().isBlank()) {
            discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stopping_message.value());
        }
    }

    public static void serverStopped() {
        if (crash) return;

        if (!CONFIG.formatting.discord.server_stopped_message.value().isBlank()) {
            discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stopped_message.value());
        }

        discord.stopJda();
    }

    // TODO: upload report.asString() to mclogs or something (configurable)
    public static void serverCrash(CrashReport report) {
        crash = true;

        if (!CONFIG.formatting.discord.server_crash_message.value().isEmpty()) {
            discord.sendMessageToDiscord(CONFIG.formatting.discord.server_crash_message.value());
        }

        discord.stopJda();
    }

    public static void playerChatMessage(ServerPlayer player, Component message) {
        if (CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value().isBlank() : CONFIG.formatting.discord.chat_message.value().isBlank()) return;

        discord.onPlayerChatMessage(player, message);
    }

    public static void playerJoin(ServerPlayer player) {
        if (CONFIG.formatting.discord.join_message.value().isBlank()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.join_message.value(),
                player
        ).getString();
        discord.sendMessageToDiscord(message);
    }

    public static void playerLeave(ServerPlayer player) {
        if (CONFIG.formatting.discord.leave_message.value().isBlank()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.leave_message.value(),
                player
        ).getString();
        discord.sendMessageToDiscord(message);
    }

    public static void playerAdvancement(ServerPlayer player, AdvancementHolder advancement) {
        DisplayInfo display = advancement.value().display().orElse(null);

        if (player.getAdvancements().getOrStartProgress(advancement).isDone()
                && display != null
                && display.shouldAnnounceChat()
                && player.level().getGameRules().get(GameRules.SHOW_ADVANCEMENT_MESSAGES)
        ) {
            String template = switch (display.getType()) {
                case CHALLENGE -> CONFIG.formatting.discord.advancement_challenge_message.value();
                case GOAL -> CONFIG.formatting.discord.advancement_goal_message.value();
                case TASK -> CONFIG.formatting.discord.advancement_task_message.value();
                default -> CONFIG.formatting.discord.advancement_fallback_message.value();
            };

            if (template.isBlank()) return;

            String message = TextUtils.parseAdvancement(
                    template,
                    player,
                    display
            ).getString();
            discord.sendMessageToDiscord(message);
        }
    }

    public static void playerDeath(ServerPlayer player, DamageSource source) {
        if (CONFIG.formatting.discord.death_message.value().isBlank()) return;

        String message = TextUtils.parseDeath(
                CONFIG.formatting.discord.death_message.value(),
                player,
                source
        ).getString();
        discord.sendMessageToDiscord(message);
    }
}