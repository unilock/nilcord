package cc.unilock.nilcord.mixin;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Inject(method = "writeToFile", at = @At("HEAD"))
    private void writeToFile(File file, CallbackInfoReturnable<Boolean> cir) {
        NilcordPremain.listener.serverCrash((CrashReport) (Object) this);
    }
}
