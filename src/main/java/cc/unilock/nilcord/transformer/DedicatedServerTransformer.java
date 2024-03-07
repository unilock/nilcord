package cc.unilock.nilcord.transformer;

import cc.unilock.nilcord.NilcordPremain;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.server.dedicated.DedicatedServer")
public class DedicatedServerTransformer extends MiniTransformer {
	@Patch.Method("startServer()Z")
	public void patchStartServer(PatchContext ctx) {
		ctx.jumpToLastReturn();

		ctx.add(
				INVOKESTATIC("cc/unilock/nilcord/transformer/DedicatedServerTransformer$Hooks", "serverStart", "()V")
		);
	}

	public static class Hooks {
		public static void serverStart() {
			// Has to be done here, since Minecraft classes don't exist during nilmod init
			NilcordPremain.initialize();
			NilcordPremain.listener.serverStart();
		}
	}
}
