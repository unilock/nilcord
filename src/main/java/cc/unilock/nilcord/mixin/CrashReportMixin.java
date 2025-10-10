package cc.unilock.nilcord.mixin;

import cc.unilock.nilcord.NilcordPremain;
import net.minecraft.util.crash.CrashReport;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(CrashReport.class)
public class CrashReportMixin {
    @Shadow
    @Nullable
    private Path file;

    @Inject(method = "writeToFile(Ljava/nio/file/Path;Lnet/minecraft/util/crash/ReportType;Ljava/util/List;)Z", at = @At("HEAD"))
    private void writeToFile(CallbackInfoReturnable<Boolean> cir) {
        if (this.file == null) {
            NilcordPremain.listener.serverCrash((CrashReport) (Object) this);
        }
    }
}
