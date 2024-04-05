package cc.unilock.nilcord;

import cc.unilock.nilcord.config.NilcordConfig;
import cc.unilock.nilcord.discord.Discord;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NilcordPremain implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("nilcord");
    public static final NilcordConfig CONFIG = NilcordConfig.createToml(FabricLoader.getInstance().getConfigDir(), "", "nilcord", NilcordConfig.class);
    public static Discord discord;
    public static EventListener listener;
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        discord = new Discord();
        listener = new EventListener();

        // Server starting / stopping events
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            NilcordPremain.server = server;
            listener.serverStart();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            listener.serverStop();
            NilcordPremain.server = null;
        });

        // Player events
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            listener.playerChatMessage(sender, message.getContent());
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            listener.playerJoin(handler.getPlayer());
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            listener.playerLeave(handler.getPlayer());
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity spe) {
                listener.playerDeath(spe, damageSource);
            }
        });
    }
}
