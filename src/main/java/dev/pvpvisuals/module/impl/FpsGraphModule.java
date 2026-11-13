package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** График FPS за последние секунды. */
public final class FpsGraphModule extends HudModule {
    private static final int N = 60;
    private final int[] samples = new int[N];
    private int idx;
    private long last;

    public FpsGraphModule() {
        super("FPS Graph", "График FPS", -1, -1);
        width = 100;
        height = 40;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw - width - 6;
        y = sh - height - 40;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        long now = System.currentTimeMillis();
        if (now - last >= 250) {
            samples[idx++ % N] = mc.getFps();
            last = now;
        }
        int max = 60;
        for (int v : samples) max = Math.max(max, v);
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        int bw = Math.max(1, (width - 8) / N);
        for (int i = 0; i < N; i++) {
            int v = samples[(idx + i) % N];
            int h = (int) ((height - 14) * (v / (float) max));
            RenderUtils.rect(g, x + 4 + i * bw, y + height - 4 - h, bw, h,
                    v >= 120 ? 0xFF55FF55 : v >= 60 ? 0xFFFFD54D : 0xFFFF4D4D);
        }
        RenderUtils.text(g, "FPS " + mc.getFps(), x + 5, y + 3, RenderUtils.WHITE);
    }
}
