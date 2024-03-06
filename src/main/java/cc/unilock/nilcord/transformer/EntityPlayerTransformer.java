package cc.unilock.nilcord.transformer;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
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
				ALOAD(0),
				ALOAD(1),
				INVOKESTATIC("cc/unilock/nilcord/transformer/EntityPlayerTransformer$Hooks", "achievement", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/stats/StatBase;)V")
		);
	}

	public static class Hooks {
		public static void achievement(EntityPlayer player, StatBase stat) {
			if (stat.isAchievement()) {
				Achievement achievement = (Achievement) stat;
				NilcordPremain.LOGGER.info(player.username+" has made the achievement "+achievement+" - "+achievement.getDescription());
			}
		}
	}
}
