package cc.unilock.nilcord;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.nilcord.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if (loadedMods.contains("ChromatiCraft")) {
            mixins.add("chromaticraft.ChromaResearchManagerMixin");
        }
        if (loadedMods.contains("serverutilities")) {
            mixins.add("serverutilities.ServerUtilitiesServerEventHandlerMixin");
        }
        return mixins;
    }
}
