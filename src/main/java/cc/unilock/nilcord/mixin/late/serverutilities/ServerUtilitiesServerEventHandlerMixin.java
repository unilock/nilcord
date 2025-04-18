package cc.unilock.nilcord.mixin.late.serverutilities;

import cc.unilock.nilcord.compat.serverutilities.ServerUtilitiesCompat;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import serverutils.handlers.ServerUtilitiesServerEventHandler;

@Mixin(value = ServerUtilitiesServerEventHandler.class, remap = false)
public class ServerUtilitiesServerEventHandlerMixin {
    @Inject(method = "onServerTick", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"), remap = false)
    private void onServerTick(CallbackInfo ci, @Local(name = "player") EntityPlayerMP player, @Local(name = "isAFK") boolean isAFK) {
        ServerUtilitiesCompat.onAfk(player, isAFK);
    }
}
