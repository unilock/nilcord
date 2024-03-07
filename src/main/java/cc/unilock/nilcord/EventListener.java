package cc.unilock.nilcord;

import net.minecraft.entity.player.EntityServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Translate;

import java.time.Duration;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;
import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class EventListener {
    public void serverStart() {
        NilcordPremain.server = (DedicatedServer) MinecraftServer.getServer();
        try {
            NilcordPremain.discord.getJda().awaitReady();
            NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_start_message.value());
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
    }

    public void serverStop() {
        try {
            NilcordPremain.discord.sendMessageToDiscord(CONFIG.formatting.discord.server_stop_message.value());
            NilcordPremain.discord.shutdown();
            NilcordPremain.discord.getJda().awaitShutdown(Duration.ofMillis(500));
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
        NilcordPremain.server = null;
    }

    public void playerChatMessage(EntityServerPlayer player, String message) {
        NilcordPremain.discord.onPlayerChatMessage(player, message);
    }

    public void playerJoin(EntityServerPlayer player) {
        String message = CONFIG.formatting.discord.join_message.value()
                .replace("<username>", player.username);
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerLeave(EntityServerPlayer player) {
        String message = CONFIG.formatting.discord.leave_message.value()
                .replace("<username>", player.username);
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerAchievement(EntityServerPlayer player, Achievement achievement) {
        // So, bad news! Statistics aren't server-side in 1.4.7 LOL

        /*
        String message = CONFIG.formatting.discord.achievement_message.value()
                .replace("<username>", player.username)
                .replace("<achievement_title>", Translate.format(achievement.statName))
                .replace("<achievement_description>", Translate.format(achievement.achievementDescription));
        NilcordPremain.discord.sendMessageToDiscord(message);
         */
    }

    public void playerDeath(EntityServerPlayer player, DamageSource source) {
        String message = CONFIG.formatting.discord.death_message.value()
                .replace("<username>", player.username)
                .replace("<death_message>", Translate.format(source.getDeathMessage(player)));
        NilcordPremain.discord.sendMessageToDiscord(message);
    }
}
