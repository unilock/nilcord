package cc.unilock.nilcord;

import cc.unilock.nilcord.config.NilcordConfig;
import cc.unilock.nilcord.discord.Discord;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("nilcord")
public class NilcordPremain {
    public static final Logger LOGGER = LoggerFactory.getLogger("nilcord");
    public static final NilcordConfig CONFIG = NilcordConfig.createToml(FMLPaths.CONFIGDIR.get(), "", "nilcord", NilcordConfig.class);
    public static Discord discord;
    public static EventListener listener;
    public static MinecraftServer server;

    public NilcordPremain() {
        discord = new Discord();
        listener = new EventListener();

        NeoForge.EVENT_BUS.register(new FMLEvents());
    }

    private static class FMLEvents {
        // Server starting / stopping events
        @SubscribeEvent
        public void serverStarted(ServerStartedEvent event) {
            NilcordPremain.server = event.getServer();
            listener.serverStart();
        }
        @SubscribeEvent
        public void serverStopping(ServerStoppingEvent event) {
            listener.serverStop();
            NilcordPremain.server = null;
        }

        // Player events
        @SubscribeEvent
        public void playerChatMessage(ServerChatEvent event) {
            listener.playerChatMessage(event.getPlayer(), event.getMessage());
        }

        @SubscribeEvent
        public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                listener.playerJoin(player);
            }
        }
        @SubscribeEvent
        public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                listener.playerLeave(player);
            }
        }

        @SubscribeEvent
        public void advancement(AdvancementEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                listener.playerAdvancement(player, event.getAdvancement());
            }
        }
        @SubscribeEvent
        public void livingDeath(LivingDeathEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                listener.playerDeath(player, event.getSource());
            }
        }
    }
}
