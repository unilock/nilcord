package cc.unilock.nilcord.compat;

import cc.unilock.nilcord.compat.chromaticraft.ChromatiCraftCompat;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.MinecraftForge;

import static cc.unilock.nilcord.NilcordPremain.LOGGER;

public class ModCompat {
    public static void init() {
        if (Loader.isModLoaded("ChromatiCraft")) {
            LOGGER.info("ChromatiCraft detected - loading support");
            MinecraftForge.EVENT_BUS.register(new ChromatiCraftCompat());
        }
        if (Loader.isModLoaded("serverutilities")) {
            LOGGER.info("ServerUtilities detected - loading support");
        }
    }
}
