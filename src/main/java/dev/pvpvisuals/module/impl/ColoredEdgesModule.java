package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Мягкое свечение цветом интерфейса по краям экрана. */
public final class ColoredEdgesModule extends Module {
    private final SliderSetting size = add(new SliderSetting("Размер, px", 30, 8, 100, 2));
    private final SliderSetting strength = add(new SliderSetting("Яркость, %", 35, 5, 100, 1));

    public ColoredEdgesModule() {
        super("Colored Edges", "Свечение по краям экрана цветом интерфейса", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        int a = (int) (2.55 * strength.getFloat());
        int top = RenderUtils.withAlpha(InterfaceModule.accent(), a);
        int bottom = RenderUtils.withAlpha(InterfaceModule.accent2(), a);
        int none = 0x00000000;
        int w = g.guiWidth(), h = g.guiHeight(), s = size.getInt();
        RenderUtils.gradientV(g, 0, 0, w, s, top, none);
        RenderUtils.gradientV(g, 0, h - s, w, s, none, bottom);
        RenderUtils.gradientH(g, 0, 0, s, h, top, none);
        RenderUtils.gradientH(g, w - s, 0, s, h, none, bottom);
    }
}
