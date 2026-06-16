package io.github.chillestorange;

import io.github.chillestorange.config.WorldSyncConfig;
import net.fabricmc.api.ModInitializer;

public class WorldSync implements ModInitializer {

    @Override
    public void onInitialize() {
        WorldSyncConfig.HANDLER.load();
    }
}