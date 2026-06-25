package io.github.chillestorange.platform.services;

import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public interface IPlatformEvents {

    void registerServerTickEnd(Consumer<MinecraftServer> listener);
}
