package cc.unilock.nilcord;

import cc.unilock.nilcord.config.NilcordConfig;
import cc.unilock.nilcord.discord.Discord;
import cc.unilock.nilcord.util.TextUtils;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityServerPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;

import java.nio.file.Paths;

public class Nilcord {
	public static final NilcordConfig CONFIG = NilcordConfig.createToml(Paths.get("config"), "", "nilcord", NilcordConfig.class);

	public static Discord discord;
//	public static MinecraftServer server;

	private static boolean crash = false;

	public static void init() {
		discord = new Discord();
	}

	public static void serverStarting() {
		discord.startJda();

		if (!CONFIG.formatting.discord.server_starting_message.value().isEmpty()) {
			discord.sendMessageToDiscord(CONFIG.formatting.discord.server_starting_message.value());
		}
	}

	public static void serverStarted() {
		if (!CONFIG.formatting.discord.server_started_message.value().isEmpty()) {
			discord.sendMessageToDiscord(CONFIG.formatting.discord.server_started_message.value());
		}
	}

	public static void serverStopping() {
		if (crash) return;

		if (!CONFIG.formatting.discord.server_stopping_message.value().isEmpty()) {
			discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stopping_message.value());
		}
	}

	public static void serverStopped() {
		if (crash) return;

		if (!CONFIG.formatting.discord.server_stopped_message.value().isEmpty()) {
			discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stopped_message.value());
		}

		discord.stopJda();
	}

	// TODO: upload report.getCompleteReport() to mclogs or something (configurable)
	public static void serverCrash(CrashReport report) {
		crash = true;

		if (!CONFIG.formatting.discord.server_crash_message.value().isEmpty()) {
			discord.sendMessageToDiscord(CONFIG.formatting.discord.server_crash_message.value());
		}

		discord.stopJda();
	}

	public static void playerChatMessage(EntityServerPlayer player, String message) {
		if (CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value().isEmpty() : CONFIG.formatting.discord.chat_message.value().isEmpty()) return;

		discord.onPlayerChatMessage(player, message);
	}

	public static void playerJoin(EntityServerPlayer player) {
		if (CONFIG.formatting.discord.join_message.value().isEmpty()) return;

		String message = TextUtils.parsePlayer(
				CONFIG.formatting.discord.join_message.value(),
				player
		);
		discord.sendMessageToDiscord(message);
	}

	public static void playerLeave(EntityServerPlayer player) {
		if (CONFIG.formatting.discord.leave_message.value().isEmpty()) return;

		String message = TextUtils.parsePlayer(
				CONFIG.formatting.discord.leave_message.value(),
				player
		);
		discord.sendMessageToDiscord(message);
	}

	public static void playerAchievement(EntityServerPlayer player, Achievement achievement) {
		// So, bad news! Statistics aren't server-side in 1.4.7 LOL

        /*
        String message = TextUtils.parseAchievement(
                CONFIG.formatting.discord.achievement_message.value(),
                player,
                achievement
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
         */
	}

	public static void playerDeath(EntityServerPlayer player, DamageSource source) {
		if (CONFIG.formatting.discord.death_message.value().isEmpty()) return;

		String message = TextUtils.parseDeath(
				CONFIG.formatting.discord.death_message.value(),
				player,
				source
		);
		discord.sendMessageToDiscord(message);
	}
}
