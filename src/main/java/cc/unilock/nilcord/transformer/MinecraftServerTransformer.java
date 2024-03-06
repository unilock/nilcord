package cc.unilock.nilcord.transformer;

import cc.unilock.nilcord.NilcordPremain;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.server.MinecraftServer")
public class MinecraftServerTransformer extends MiniTransformer {
	@Patch.Method("stopServer()V")
	public void patchStopServer(PatchContext ctx) {
		ctx.search(
				GETSTATIC("net/minecraft/server/MinecraftServer", "logger", "Ljava/util/logging/Logger;"),
				LDC("Stopping server")
		).jumpBefore();

		ctx.add(
				INVOKESTATIC("cc/unilock/nilcord/transformer/MinecraftServerTransformer$Hooks", "serverStop", "()V")
		);
	}

	public static class Hooks {
		public static void serverStop() {
			NilcordPremain.LOGGER.info("Server stopping!");
			NilcordPremain.server = null;
		}
	}
}
