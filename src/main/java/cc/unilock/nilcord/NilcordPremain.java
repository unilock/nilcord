package cc.unilock.nilcord;

import cc.unilock.nilcord.transformer.ClassReaderTransformer;
import cc.unilock.nilcord.transformer.DedicatedServerTransformer;
import cc.unilock.nilcord.transformer.EntityPlayerTransformer;
import cc.unilock.nilcord.transformer.EntityServerPlayerTransformer;
import cc.unilock.nilcord.transformer.MinecraftServerTransformer;
import cc.unilock.nilcord.transformer.NetServerHandlerTransformer;
import cc.unilock.nilcord.transformer.ServerConfigurationManagerTransformer;
import nilloader.api.ClassTransformer;
import nilloader.api.ModRemapper;
import nilloader.api.NilLogger;

public class NilcordPremain implements Runnable {
	public static final NilLogger LOGGER = NilLogger.get("Nilcord");
	public static EventListener listener;

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
}
