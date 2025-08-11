package cc.unilock.nilcord.mixin.early.minecraft;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Inject(method = "saveToFile", at = @At(value = "NEW", target = "(Ljava/io/File;)Ljava/io/FileOutputStream;", remap = false))
    private void saveToFile(CallbackInfoReturnable<Boolean> cir) {
        NilcordPremain.listener.serverCrash((CrashReport) (Object) this);
    }
}
