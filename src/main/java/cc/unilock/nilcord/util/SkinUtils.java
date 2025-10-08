package cc.unilock.nilcord.util;

import cc.unilock.nilcord.NilcordPremain;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import java.util.Map;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;

public class SkinUtils {
    public static Skin getSkin(GameProfile profile) {
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = NilcordPremain.server.getSessionService().getTextures(profile, false);

        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            MinecraftProfileTexture skin = map.get(MinecraftProfileTexture.Type.SKIN);
            String model = skin.getMetadata("model");
            return new Skin(
                    skin.getUrl().substring(38),
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
