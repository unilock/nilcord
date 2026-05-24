package cc.unilock.nilcord;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.level.ServerPlayer;

public class NilcordFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Nilcord.init();

        // Server starting / stopping events
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Nilcord.server = server;
            Nilcord.serverInit();
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> Nilcord.serverStart());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            Nilcord.serverStop();
            Nilcord.server = null;
        });

        // Player events
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            Nilcord.playerChatMessage(sender, message.decoratedContent());
        });
        ServerPlayerEvents.JOIN.register(Nilcord::playerJoin);
        ServerPlayerEvents.LEAVE.register(Nilcord::playerLeave);
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayer player) {
                Nilcord.playerDeath(player, damageSource);
            }
        });
    }
}
