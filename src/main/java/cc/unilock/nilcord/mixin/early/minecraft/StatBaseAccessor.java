package cc.unilock.nilcord.mixin.early.minecraft;

import net.minecraft.stats.StatBase;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StatBase.class)
public interface StatBaseAccessor {
    @Accessor
    IChatComponent getStatName();
}
