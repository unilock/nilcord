package cc.unilock.nilcord.util;

import cc.unilock.nilcord.NilcordPremain;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static cc.unilock.nilcord.NilcordPremain.CONFIG;

public class SkinUtils {
    private static final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L,TimeUnit.SECONDS).build(new CacheLoader<>() {
        @NotNull
        public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(@NotNull String string) {
            GameProfile gameProfile = new GameProfile(null, "dummy_mcdummyface");
            gameProfile.getProperties().put("textures", new Property("textures", string, ""));

            try {
                return NilcordPremain.server.getSessionService().getTextures(gameProfile, false);
            } catch (Throwable var4) {
                return ImmutableMap.of();
            }
        }
    });

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile) {
        Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
        return property == null ? ImmutableMap.of() : skinCache.getUnchecked(property.getValue());
    }

    public static String getSkin(GameProfile profile) {
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = getTextures(profile);
        return map.containsKey(MinecraftProfileTexture.Type.SKIN)
                ? map.get(MinecraftProfileTexture.Type.SKIN).getUrl().substring(38)
                : CONFIG.formatting.discord.webhook.default_skin_id.value();
    }
}
