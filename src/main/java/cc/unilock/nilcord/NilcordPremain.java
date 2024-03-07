package cc.unilock.nilcord;

import cc.unilock.nilcord.config.NilcordConfig;
import cc.unilock.nilcord.discord.Discord;
import cc.unilock.nilcord.transformer.ClassReaderTransformer;
import cc.unilock.nilcord.transformer.DedicatedServerTransformer;
import cc.unilock.nilcord.transformer.EntityPlayerTransformer;
import cc.unilock.nilcord.transformer.EntityServerPlayerTransformer;
import cc.unilock.nilcord.transformer.MinecraftServerTransformer;
import cc.unilock.nilcord.transformer.NetServerHandlerTransformer;
import cc.unilock.nilcord.transformer.ServerConfigurationManagerTransformer;
import net.minecraft.server.dedicated.DedicatedServer;
import nilloader.api.ClassTransformer;
import nilloader.api.ModRemapper;
import nilloader.api.NilLogger;

import java.nio.file.Paths;

public class NilcordPremain implements Runnable {
	public static final NilLogger LOGGER = NilLogger.get("Nilcord");
	public static final NilcordConfig CONFIG = NilcordConfig.createToml(Paths.get("config"), "", "nilcord", NilcordConfig.class);
	public static Discord discord;
	public static EventListener listener;
	public static DedicatedServer server;

	@Override
	public void run() {
		ModRemapper.setTargetMapping("default");

		// Required for Forge compatibility
		ClassTransformer.register(new ClassReaderTransformer());

		// Server starting / stopping events
		ClassTransformer.register(new DedicatedServerTransformer());
		ClassTransformer.register(new MinecraftServerTransformer());

		// Player events
		ClassTransformer.register(new EntityPlayerTransformer());
		ClassTransformer.register(new EntityServerPlayerTransformer());
		ClassTransformer.register(new NetServerHandlerTransformer());
		ClassTransformer.register(new ServerConfigurationManagerTransformer());
	}

	public static void initialize() {
		discord = new Discord();
		listener = new EventListener();
	}
}
