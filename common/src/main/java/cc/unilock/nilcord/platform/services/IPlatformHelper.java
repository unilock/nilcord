package cc.unilock.nilcord.platform.services;

import java.nio.file.Path;

public interface IPlatformHelper {
    Path getConfigDir();
    boolean isModLoaded(String modId);
}