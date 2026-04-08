package cc.unilock.nilcord.mixin;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAdvancements.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayer player;

    @Inject(method = "award(Lnet/minecraft/advancements/AdvancementHolder;Ljava/lang/String;)Z", at = @At("RETURN"))
    private void award(AdvancementHolder holder, String criterion, CallbackInfoReturnable<Boolean> cir) {
        NilcordPremain.listener.playerAdvancement(this.player, holder);
    }
}
