package cc.unilock.nilcord.mixin.early.minecraft;

import cc.unilock.nilcord.NilcordPremain;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.crash.CrashReport;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;finalTick(Lnet/minecraft/crash/CrashReport;)V", ordinal = 1))
    private void run$finalTick(CallbackInfo ci, @Local CrashReport crashreport) {
        NilcordPremain.listener.serverCrash(crashreport);
    }
}
