package io.github.chillestorange.neoforge.platform;

import io.github.chillestorange.platform.services.IPlatformEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.function.Consumer;

public class NeoForgePlatformEvents implements IPlatformEvents {

    @Override
    public void registerServerTickEnd(Consumer<MinecraftServer> listener) {
        NeoForge.EVENT_BUS.addListener(
                (ServerTickEvent.Post event) -> listener.accept(event.getServer())
        );
    }
}
