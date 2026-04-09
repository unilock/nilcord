package cc.unilock.nilcord;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(Constants.MOD_ID)
public class NilcordNeoForge {
    public NilcordNeoForge() {
        Nilcord.init();

        NeoForge.EVENT_BUS.register(new FMLEvents());
    }

    private static class FMLEvents {
        // Server starting / stopping events
        @SubscribeEvent
        public void serverInit(ServerStartingEvent event) {
            Nilcord.server = event.getServer();
            Nilcord.serverInit();
        }
        @SubscribeEvent
        public void serverStarted(ServerStartedEvent event) {
            Nilcord.serverStart();
        }
        @SubscribeEvent
        public void serverStopping(ServerStoppingEvent event) {
            Nilcord.serverStop();
            Nilcord.server = null;
        }

        // Player events
        @SubscribeEvent
        public void playerChatMessage(ServerChatEvent event) {
            Nilcord.playerChatMessage(event.getPlayer(), event.getMessage());
        }

        @SubscribeEvent
        public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                Nilcord.playerJoin(player);
            }
        }
        @SubscribeEvent
        public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                Nilcord.playerLeave(player);
            }
        }

        @SubscribeEvent
        public void advancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                Nilcord.playerAdvancement(player, event.getAdvancement());
            }
        }
        @SubscribeEvent
        public void livingDeath(LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                Nilcord.playerDeath(player, event.getSource());
            }
        }
    }
}