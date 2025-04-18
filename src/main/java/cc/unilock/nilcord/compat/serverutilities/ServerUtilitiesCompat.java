package cc.unilock.nilcord.compat.serverutilities;

import cc.unilock.nilcord.NilcordPremain;
import cc.unilock.nilcord.util.TextUtils;
import net.minecraft.entity.player.EntityPlayerMP;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;

public class ServerUtilitiesCompat {
    public static void onAfk(EntityPlayerMP player, boolean isAFK) {
        sendMessage(player, isAFK ? CONFIG.formatting.discord.compat.serverutilities.afk_start.value() : CONFIG.formatting.discord.compat.serverutilities.afk_stop.value());
    }

    private static void sendMessage(EntityPlayerMP player, String template) {
        if (!template.isEmpty()) {
            String msg = TextUtils.parsePlayer(template, player);

            NilcordPremain.discord.sendMessageToDiscord(msg);
        }
    }
}
