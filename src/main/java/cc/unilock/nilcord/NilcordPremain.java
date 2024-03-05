package cc.unilock.nilcord;

import cc.unilock.nilcord.transformer.EntityPlayerTransformer;
import cc.unilock.nilcord.transformer.EntityServerPlayerTransformer;
import cc.unilock.nilcord.transformer.NetServerHandlerTransformer;
import nilloader.api.ClassTransformer;
import nilloader.api.ModRemapper;
import nilloader.api.NilLogger;

public class NilcordPremain implements Runnable {
	public static final NilLogger LOGGER = NilLogger.get("Nilcord");

	@Override
	public void run() {
		ModRemapper.setTargetMapping("default");

		ClassTransformer.register(new EntityPlayerTransformer());
		ClassTransformer.register(new EntityServerPlayerTransformer());
		ClassTransformer.register(new NetServerHandlerTransformer());
	}
}
