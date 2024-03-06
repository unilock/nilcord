package cc.unilock.nilcord.transformer;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.entity.player.EntityServerPlayer;
import net.minecraft.util.DamageSource;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

@Patch.Class("net.minecraft.entity.player.EntityServerPlayer")
public class EntityServerPlayerTransformer extends MiniTransformer {
	@Patch.Method("onDeath(Lnet/minecraft/util/DamageSource;)V")
	public void patchOnDeath(PatchContext ctx) {
		ctx.jumpToStart();

		ctx.add(
				ALOAD(0),
				ALOAD(1),
				INVOKESTATIC("cc/unilock/nilcord/transformer/EntityServerPlayerTransformer$Hooks", "death", "(Lnet/minecraft/entity/player/EntityServerPlayer;Lnet/minecraft/util/DamageSource;)V")
		);
	}

	public static class Hooks {
		public static void death(EntityServerPlayer player, DamageSource source) {
			NilcordPremain.listener.playerDeath(player, source);
		}
	}
}
