package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Чёрные полосы сверху и снизу, как в кино. */
public final class CinematicBarsModule extends Module {
    private final SliderSetting size = add(new SliderSetting("Высота, %", 10, 2, 25, 1));

    public CinematicBarsModule() {
        super("Cinematic Bars", "Чёрные полосы как в кино", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        int h = (int) (g.guiHeight() * size.getFloat() / 100f);
        g.fill(0, 0, g.guiWidth(), h, 0xFF000000);
        g.fill(0, g.guiHeight() - h, g.guiWidth(), g.guiHeight(), 0xFF000000);
    }
}
