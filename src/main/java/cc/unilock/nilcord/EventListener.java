package cc.unilock.nilcord;

import cc.unilock.nilcord.mixin.accessor.AchievementAccessor;
import cc.unilock.nilcord.mixin.accessor.StatBaseAccessor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

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
            NilcordPremain.discord.getJda().awaitShutdown(Duration.ofSeconds(3));
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
        NilcordPremain.server = null;
    }

    public void playerChatMessage(EntityPlayerMP player, String message) {
        NilcordPremain.discord.onPlayerChatMessage(player, message);
    }

    public void playerJoin(EntityPlayerMP player) {
        String message = CONFIG.formatting.discord.join_message.value()
                .replace("<username>", player.getCommandSenderName());
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerLeave(EntityPlayerMP player) {
        String message = CONFIG.formatting.discord.leave_message.value()
                .replace("<username>", player.getCommandSenderName());
        NilcordPremain.discord.sendMessageToDiscord(message);
    }

    public void playerAchievement(EntityPlayerMP player, Achievement achievement) {
        if (player.func_147099_x().canUnlockAchievement(achievement)
            && !player.func_147099_x().hasAchievementUnlocked(achievement)
            && player.mcServer.func_147136_ar()
        ) {
            String message = CONFIG.formatting.discord.achievement_message.value()
                .replace("<username>", player.getCommandSenderName())
                .replace("<achievement_title>", ((StatBaseAccessor) achievement).getStatName().getUnformattedTextForChat())
                .replace(
                    "<achievement_description>",
                    achievement.statId.equals("achievement.openInventory")
                        ? StatCollector.translateToLocal(((AchievementAccessor) achievement).getAchievementDescription()).replace("%1$s", "E")
                        : StatCollector.translateToLocal(((AchievementAccessor) achievement).getAchievementDescription())
            );
            NilcordPremain.discord.sendMessageToDiscord(message);
        }
    }

    public void playerDeath(EntityPlayerMP player, DamageSource source) {
        String message = CONFIG.formatting.discord.death_message.value()
                .replace("<username>", player.getCommandSenderName())
                .replace("<death_message>", source.func_151519_b(player).getUnformattedTextForChat());
        NilcordPremain.discord.sendMessageToDiscord(message);
    }
}
