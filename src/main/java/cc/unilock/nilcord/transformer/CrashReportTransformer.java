package cc.unilock.nilcord.transformer;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.crash.CrashReport;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.crash.CrashReport")
public class CrashReportTransformer extends MiniTransformer {
	@Patch.Method("<init>(Ljava/lang/String;Ljava/lang/Throwable;)V")
	public void patchInit(PatchContext ctx) {
		ctx.jumpToLastReturn();

		ctx.add(
				ALOAD(0),
				INVOKESTATIC("cc/unilock/nilcord/transformer/CrashReportTransformer$Hooks", "serverCrash", "(Lnet/minecraft/crash/CrashReport;)V")
		);
	}

	public static class Hooks {
		public static void serverCrash(CrashReport report) {
			NilcordPremain.listener.serverCrash(report);
		}
	}
}
