package cc.unilock.nilcord;

import cc.unilock.nilcord.config.NilcordConfig;
import cc.unilock.nilcord.discord.Discord;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.server.FMLServerHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, acceptableRemoteVersions = "*")
public class NilcordPremain {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
	public static final NilcordConfig CONFIG = NilcordConfig.createToml(Paths.get("config"), "", "nilcord", NilcordConfig.class);
	public static Discord discord;
	public static EventListener listener;
	public static DedicatedServer server;

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        discord = new Discord();
        listener = new EventListener();

        MinecraftForge.EVENT_BUS.register(new FMLEvents());
        MinecraftForge.EVENT_BUS.register(new MFEvents());
    }

    // Server starting / stopping events
    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        NilcordPremain.server = (DedicatedServer) FMLServerHandler.instance().getServer();
        listener.serverStart();
    }
    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        listener.serverStop();
        NilcordPremain.server = null;
    }

    // Player events
    public static final class FMLEvents {
        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.player instanceof EntityPlayerMP player) {
                listener.playerJoin(player);
            }
        }
        @SubscribeEvent
        public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (event.player instanceof EntityPlayerMP player) {
                listener.playerLeave(player);
            }
        }
    }
    public static final class MFEvents {
        @SubscribeEvent
        public void onServerChat(ServerChatEvent event) {
            listener.playerChatMessage(event.getPlayer(), event.getComponent());
        }
        @SubscribeEvent
        public void onLivingDeath(LivingDeathEvent event) {
            if (event.getEntityLiving() instanceof EntityPlayerMP player) {
                listener.playerDeath(player, event.getSource());
            }
        }
        @SubscribeEvent
        public void onAchievement(AdvancementEvent event) {
            if (event.getEntityPlayer() instanceof EntityPlayerMP player) {
                listener.playerAdvancement(player, event.getAdvancement());
            }
        }
    }
}
