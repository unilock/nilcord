package cc.unilock.nilcord.mixin;

import cc.unilock.nilcord.Nilcord;
import net.minecraft.CrashReport;
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
    private Path saveFile;

    @Inject(method = "saveToFile(Ljava/nio/file/Path;Lnet/minecraft/ReportType;Ljava/util/List;)Z", at = @At("HEAD"))
    private void writeToFile(CallbackInfoReturnable<Boolean> cir) {
        if (this.saveFile == null) {
            Nilcord.serverCrash((CrashReport) (Object) this);
        }
    }
}
