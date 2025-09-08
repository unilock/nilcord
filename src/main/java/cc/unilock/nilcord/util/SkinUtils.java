package cc.unilock.nilcord.util;

import cc.unilock.nilcord.NilcordPremain;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;

import java.util.Optional;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;

public class SkinUtils {
    public static MinecraftProfileTextures getTextures(GameProfile profile) {
        return NilcordPremain.server.getSessionService().getTextures(profile);
    }

    public static String getSkin(GameProfile profile) {
        return Optional.ofNullable(getTextures(profile).skin()).map(mpt -> mpt.getUrl().substring(38)).orElse(CONFIG.formatting.discord.webhook.default_skin_id.value());
    }
}
