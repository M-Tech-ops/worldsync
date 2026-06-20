package io.github.chillestorange.client.ui;

import io.github.chillestorange.service.WorldSyncService;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class SyncHudOverlay implements HudElement {

    private static final int PADDING = 4;

    public static void register() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("worldsync", "sync_status"), new SyncHudOverlay());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!WorldSyncService.issyncing()) {
            return;
        }

        var font = Minecraft.getInstance().font;
        String label = "⟳ Syncing...";
        int textWidth = font.width(label);

        int x = graphics.guiWidth() - textWidth - PADDING - 4;
        int y = PADDING;

        graphics.fill(x - 2, y - 2, x + textWidth + 2, y + 10, 0x80000000);
        graphics.text(font, label, x, y, 0xFFFFFF);
    }
}