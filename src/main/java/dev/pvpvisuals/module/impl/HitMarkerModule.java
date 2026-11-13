package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.util.HitTracker;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Короткий крестик у прицела при попадании по цели. */
public final class HitMarkerModule extends Module {
    private final ColorSetting color = add(new ColorSetting("Цвет", 0xFFFFFFFF));

    public HitMarkerModule() {
        super("Hit Marker", "Крестик у прицела при попадании", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        long age = System.currentTimeMillis() - HitTracker.lastHitTime();
        if (age > 250 || age < 0) return;
        int a = (int) (255 * (1.0 - age / 250.0));
        int c = RenderUtils.withAlpha(color.get(), a);
        int cx = g.guiWidth() / 2, cy = g.guiHeight() / 2;
        for (int i = 3; i <= 8; i++) {
            RenderUtils.rect(g, cx - i, cy - i, 1, 1, c);
            RenderUtils.rect(g, cx + i, cy - i, 1, 1, c);
            RenderUtils.rect(g, cx - i, cy + i, 1, 1, c);
            RenderUtils.rect(g, cx + i, cy + i, 1, 1, c);
        }
    }
}
