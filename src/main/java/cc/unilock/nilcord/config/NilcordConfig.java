package cc.unilock.nilcord.config;

import folk.sisby.kaleido.api.ReflectiveConfig;

import java.nio.file.Paths;

public class NilcordConfig extends ReflectiveConfig {
    public static final NilcordConfig INSTANCE = NilcordConfig.createToml(Paths.get("config"), "", "nilcord", NilcordConfig.class);

    //@Comment("Example Config Value(TM)")
    //public final TrackedValue<String> example = value("example");
}
