package cc.unilock.nilcord;

import net.minecraft.stats.StatBase;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.entity.player.EntityPlayer")
public class EntityPlayerTransformer extends MiniTransformer {
	@Patch.Method("triggerAchievement(Lnet/minecraft/stats/StatBase;)V")
	public void patchTriggerAchievement(PatchContext ctx) {
		ctx.jumpToStart();

		ctx.add(
				ALOAD(1),
				INVOKESTATIC("cc/unilock/nilcord/EntityPlayerTransformer$Hooks", "achievement", "(Lnet/minecraft/stats/StatBase;)V")
		);
	}

	public static class Hooks {
		public static void achievement(StatBase achievement) {
			NilcordPremain.LOGGER.info("Achievement: "+achievement.toString());
		}
	}
}
