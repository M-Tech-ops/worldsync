package io.github.chillestorange.client.ui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SyncingScreen extends Screen {

    private static final long FRAME_DURATION_MS = 400;
    private static final String[] DOT_FRAMES = {".", "..", "..."};

    private final long startTimeMillis = System.currentTimeMillis();

    public SyncingScreen() {
        super(Component.literal("Syncing World"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        long elapsed = System.currentTimeMillis() - startTimeMillis;
        String dots = DOT_FRAMES[(int) (elapsed / FRAME_DURATION_MS) % DOT_FRAMES.length];

        graphics.centeredText(this.font, "Syncing world" + dots, centerX, centerY - 10, 0xFFFFFFFF);
        renderSpinner(graphics, centerX, centerY + 14, elapsed);
    }

    private void renderSpinner(GuiGraphicsExtractor graphics, int centerX, int centerY, long elapsed) {
        float baseAngle = (elapsed % 1000) / 1000f * 360f;
        int radius = 6;
        int dotCount = 8;

        for (int i = 0; i < dotCount; i++) {
            double theta = Math.toRadians(baseAngle + (360.0 / dotCount) * i);
            int x = centerX + (int) (Math.cos(theta) * radius);
            int y = centerY + (int) (Math.sin(theta) * radius);
            int brightness = 255 - (i * 255 / dotCount);
            int color = 0xFF000000 | (brightness << 16) | (brightness << 8) | brightness;
            graphics.fill(x - 1, y - 1, x + 1, y + 1, color);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}