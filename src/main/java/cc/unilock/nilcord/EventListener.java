package cc.unilock.nilcord;

import cc.unilock.nilcord.util.TextUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;

import java.time.Duration;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;
import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class EventListener {
    public void serverStart() {
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
    }

    public void playerChatMessage(EntityPlayerMP player, String message) {
        if (CONFIG.discord.webhook.enabled.value() ? CONFIG.formatting.discord.webhook.chat_message.value().isEmpty() : CONFIG.formatting.discord.chat_message.value().isEmpty()) return;

        NilcordPremain.discord.onPlayerChatMessage(player, message);
    }

    public void playerJoin(EntityPlayerMP player) {
        if (CONFIG.formatting.discord.join_message.value().isEmpty()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.join_message.value(),
                player
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerLeave(EntityPlayerMP player) {
        if (CONFIG.formatting.discord.leave_message.value().isEmpty()) return;

        String message = TextUtils.parsePlayer(
                CONFIG.formatting.discord.leave_message.value(),
                player
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerAchievement(EntityPlayerMP player, Achievement achievement) {
        if (CONFIG.formatting.discord.achievement_message.value().isEmpty()) return;

        if (player.func_147099_x().canUnlockAchievement(achievement)
                && !player.func_147099_x().hasAchievementUnlocked(achievement)
                && player.mcServer.func_147136_ar()
        ) {
            String message = TextUtils.parseAdvancement(
                    CONFIG.formatting.discord.achievement_message.value(),
                    player,
                    achievement
            );
            NilcordPremain.discord.sendMessageToDiscord(message);
        }
    }

    public void playerDeath(EntityPlayerMP player, DamageSource source) {
        if (CONFIG.formatting.discord.death_message.value().isEmpty()) return;

        String message = TextUtils.parseDeath(
                CONFIG.formatting.discord.death_message.value(),
                player,
                source
        );
        NilcordPremain.discord.sendMessageToDiscord(message);
    }
}
