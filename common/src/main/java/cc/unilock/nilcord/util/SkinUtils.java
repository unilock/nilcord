package cc.unilock.nilcord.util;

import cc.unilock.nilcord.Nilcord;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;

import static cc.unilock.nilcord.Nilcord.CONFIG;

public class SkinUtils {
    public static Skin getSkin(GameProfile profile) {
        MinecraftProfileTextures mpt = Nilcord.server.services().sessionService().getTextures(profile);

        if (mpt.skin() != null) {
            String model = mpt.skin().getMetadata("model");
            return new Skin(
                    mpt.skin().getUrl().substring(38),
                    model == null ? CONFIG.formatting.discord.webhook.default_skin_model.value() : model
            );
        } else {
            return new Skin(
                    CONFIG.formatting.discord.webhook.default_skin_id.value(),
                    CONFIG.formatting.discord.webhook.default_skin_model.value()
            );
        }
    }

    public record Skin(String id, String model) {}
}
