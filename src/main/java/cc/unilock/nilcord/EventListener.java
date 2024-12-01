package cc.unilock.nilcord;

import cc.unilock.nilcord.util.TextUtils;
import net.minecraft.entity.player.EntityServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;

import java.time.Duration;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;
import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class EventListener {
    public void serverStart() {
        NilcordPremain.server = (DedicatedServer) MinecraftServer.getServer();
        try {
            NilcordPremain.discord.getJda().awaitReady();
            if (!CONFIG.formatting.discord.server_start_message.value().isEmpty()) {
                NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_start_message.value());
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    public void serverStop() {
        try {
            if (!CONFIG.formatting.discord.server_stop_message.value().isEmpty()) {
                NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stop_message.value());
            }
            NilcordPremain.discord.shutdown();
            NilcordPremain.discord.getJda().awaitShutdown(Duration.ofSeconds(3));
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
        NilcordPremain.server = null;
    }

    public void playerChatMessage(EntityServerPlayer player, String message) {
        if (CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value().isEmpty() : CONFIG.formatting.discord.chat_message.value().isEmpty()) return;

        NilcordPremain.discord.onPlayerChatMessage(player, message);
    }

    public void playerJoin(EntityServerPlayer player) {
        if (CONFIG.formatting.discord.join_message.value().isEmpty()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.join_message.value(),
                player
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerLeave(EntityServerPlayer player) {
        if (CONFIG.formatting.discord.leave_message.value().isEmpty()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.leave_message.value(),
                player
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerAchievement(EntityServerPlayer player, Achievement achievement) {
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

    public void playerDeath(EntityServerPlayer player, DamageSource source) {
        if (CONFIG.formatting.discord.death_message.value().isEmpty()) return;

        String message = TextUtils.parseDeath(
                CONFIG.formatting.discord.death_message.value(),
                player,
                source
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }
}
