package cc.unilock.nilcord.mixin.early.minecraft;

import net.minecraft.stats.Achievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Achievement.class)
public interface AchievementAccessor {
    @Accessor
    String getAchievementDescription();
}
