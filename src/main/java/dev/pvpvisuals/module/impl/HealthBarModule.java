package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

import java.util.Locale;

/** Полоски здоровья и голода с числами. */
public final class HealthBarModule extends HudModule {
    public HealthBarModule() {
        super("Health Bar", "Здоровье, поглощение и голод полосками", -1, -1);
        width = 110;
        height = 34;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 - width / 2;
        y = sh - 80;
    }

    private void bar(GuiGraphics g, int by, float value, float max, int color, String text) {
        RenderUtils.roundRect(g, x + 4, by, width - 8, 9, 3, 0xAA000000);
        int w = (int) ((width - 8) * Math.max(0f, Math.min(1f, value / max)));
        if (w > 1) RenderUtils.roundRect(g, x + 4, by, w, 9, 3, color);
        RenderUtils.textCentered(g, text, x + width / 2, by + 1, RenderUtils.WHITE);
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        float hp = p.getHealth(), max = p.getMaxHealth(), abs = p.getAbsorptionAmount();
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        int hpColor = hp > max * 0.5f ? 0xFF55FF55 : hp > max * 0.25f ? 0xFFFFD54D : 0xFFFF4D4D;
        bar(g, y + 4, hp, max, hpColor, String.format(Locale.ROOT, abs > 0 ? "%.1f + %.1f" : "%.1f", hp, abs > 0 ? abs : 0f));
        bar(g, y + 21, p.getFoodData().getFoodLevel(), 20f, 0xFFD9A441, String.valueOf(p.getFoodData().getFoodLevel()));
    }
}
