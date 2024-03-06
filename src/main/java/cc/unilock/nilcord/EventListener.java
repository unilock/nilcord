package cc.unilock.nilcord;

import net.minecraft.entity.player.EntityServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Translate;

public class EventListener {
    private MinecraftServer server = null;

    public void serverStart() {
        NilcordPremain.LOGGER.info("Server started!");
        this.server = MinecraftServer.getServer();
    }

    public void serverStop() {
        NilcordPremain.LOGGER.info("Server stopping!");
        this.server = null;
    }

    public void playerChatMessage(EntityServerPlayer player, String message) {
        NilcordPremain.LOGGER.info("Chat Message: \""+message+"\" from "+player.username);
    }

    public void playerJoin(EntityServerPlayer player) {
        NilcordPremain.LOGGER.info(player.username+" joined the game");
    }

    public void playerLeave(EntityServerPlayer player) {
        NilcordPremain.LOGGER.info(player.username+" left the game");
    }

    public void playerAchievement(EntityServerPlayer player, Achievement achievement) {
        NilcordPremain.LOGGER.info(player.username+" has made the achievement "+Translate.format(achievement.statName)+" - "+Translate.format(achievement.achievementDescription));
    }

    public void playerDeath(EntityServerPlayer player, DamageSource source) {
        NilcordPremain.LOGGER.info(player.username+" died: "+source.getDeathMessage(player));
    }
}
