package cc.unilock.nilcord;

import cc.unilock.nilcord.compat.ModCompat;
import cc.unilock.nilcord.config.NilcordConfig;
import cc.unilock.nilcord.discord.Discord;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

@Mod(modid = "nilcord", version = Tags.VERSION, name = "Nilcord",  acceptedMinecraftVersions = "[1.7.10]", acceptableRemoteVersions = "*")
public class NilcordPremain {
    public static final Logger LOGGER = LogManager.getLogger("nilcord");
	public static final NilcordConfig CONFIG = NilcordConfig.createToml(Paths.get("config"), "", "nilcord", NilcordConfig.class);
	public static Discord discord;
	public static EventListener listener;
	public static DedicatedServer server;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        discord = new Discord();
        listener = new EventListener();

        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        ModCompat.init();
    }

    // Server starting / stopping events
    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        listener.serverStart();
    }
    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        listener.serverStop();
    }

    // Player events
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        listener.playerChatMessage(event.player, event.message);
    }
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
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP player) {
            listener.playerDeath(player, event.source);
        }
    }
    @SubscribeEvent
    public void onAchievement(AchievementEvent event) {
        if (event.entityPlayer instanceof EntityPlayerMP player) {
            listener.playerAchievement(player, event.achievement);
        }
    }
}
