package cc.unilock.nilcord.mixin;

import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.ResearchLevel;
import cc.unilock.nilcord.compat.chromaticraft.ChromatiCraftCompat;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChromaResearchManager.class, remap = false)
public class ChromaResearchManagerMixin {
    @Inject(method = "setPlayerResearchLevel", at = @At(value = "INVOKE", target = "LReika/ChromatiCraft/Magic/Progression/ChromaResearchManager;notifyPlayerOfProgression(Lnet/minecraft/entity/player/EntityPlayer;LReika/ChromatiCraft/Magic/Progression/ChromaResearchManager$ProgressElement;)V"), require = 0)
    private void setPlayerResearchLevel(EntityPlayer ep, ResearchLevel r, boolean notify, CallbackInfoReturnable<Boolean> cir) {
        ChromatiCraftCompat.onProgressionLevel(ep ,r);
    }
}
