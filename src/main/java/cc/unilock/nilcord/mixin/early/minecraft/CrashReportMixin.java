package cc.unilock.nilcord.mixin.early.minecraft;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void writeToFile(String description, Throwable cause, CallbackInfo ci) {
        NilcordPremain.listener.serverCrash((CrashReport) (Object) this);
    }
}
