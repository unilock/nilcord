package cc.unilock.nilcord;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EarlyMixinLoader implements IEarlyMixinLoader, IFMLLoadingPlugin {
	@Override
	public List<String> getMixinConfigs() {
		return Collections.singletonList("mixins.nilcord.early.json");
	}

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// NO-OP
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
