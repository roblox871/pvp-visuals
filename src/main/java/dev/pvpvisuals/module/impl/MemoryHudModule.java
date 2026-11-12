package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Использование памяти игрой с полоской заполнения. */
public final class MemoryHudModule extends HudModule {
    private long usedMb, maxMb;
    private int tick;

    public MemoryHudModule() {
        super("Memory", "Использование оперативной памяти игрой", 6, 200);
        width = 90;
        height = 24;
    }

    @Override
    public void onTick() {
        if (tick++ % 20 != 0) return;
        Runtime rt = Runtime.getRuntime();
        usedMb = (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024);
        maxMb = rt.maxMemory() / (1024 * 1024);
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        RenderUtils.text(g, "RAM", x + 5, y + 4, RenderUtils.GRAY);
        RenderUtils.textRight(g, usedMb + "/" + maxMb + " MB", x + width - 5, y + 4, RenderUtils.WHITE);
        float ratio = maxMb <= 0 ? 0f : Math.max(0f, Math.min(1f, usedMb / (float) maxMb));
        int color = ratio < 0.6f ? 0xFF55FF55 : ratio < 0.85f ? 0xFFFFFF55 : 0xFFFF5555;
        RenderUtils.rect(g, x + 5, y + 16, width - 10, 3, RenderUtils.DARK);
        RenderUtils.rect(g, x + 5, y + 16, Math.round((width - 10) * ratio), 3, color);
    }
}
