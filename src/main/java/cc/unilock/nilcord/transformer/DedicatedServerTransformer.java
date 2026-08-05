package cc.unilock.nilcord.transformer;

import cc.unilock.nilcord.Nilcord;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.server.dedicated.DedicatedServer")
public class DedicatedServerTransformer extends MiniTransformer {
	@Patch.Method("startServer()Z")
	public void patchStartServer(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
				INVOKESTATIC("cc/unilock/nilcord/transformer/DedicatedServerTransformer$Hooks", "serverStarting", "()V")
		);

		ctx.jumpToLastReturn();
		ctx.add(
				INVOKESTATIC("cc/unilock/nilcord/transformer/DedicatedServerTransformer$Hooks", "serverStarted", "()V")
		);
	}

	@Patch.Method("systemExitNow()V")
	public void patchSystemExitNow(PatchContext ctx) {
		ctx.jumpToStart();
		ctx.add(
				INVOKESTATIC("cc/unilock/nilcord/transformer/DedicatedServerTransformer$Hooks", "serverStopped", "()V")
		);
	}

	public static class Hooks {
		public static void serverStarting() {
			Nilcord.init();
			Nilcord.serverStarting();
		}

		public static void serverStarted() {
			Nilcord.serverStarted();
		}

		public static void serverStopped() {
			Nilcord.serverStopped();
		}
	}
}
