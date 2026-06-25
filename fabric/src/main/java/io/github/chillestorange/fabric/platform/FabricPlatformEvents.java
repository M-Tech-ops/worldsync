package io.github.chillestorange.fabric.platform;

import io.github.chillestorange.platform.services.IPlatformEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class FabricPlatformEvents implements IPlatformEvents {
    
    @Override
    public void registerServerTickEnd(Consumer<MinecraftServer> listener) {
        ServerTickEvents.END_SERVER_TICK.register(listener::accept);
    }
}
