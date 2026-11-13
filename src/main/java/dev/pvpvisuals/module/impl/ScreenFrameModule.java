package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Тонкая рамка вокруг экрана цветом интерфейса. */
public final class ScreenFrameModule extends Module {
    private final SliderSetting thick = add(new SliderSetting("Толщина", 2, 1, 6, 1));

    public ScreenFrameModule() {
        super("Screen Frame", "Рамка вокруг экрана", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        int w = g.guiWidth(), h = g.guiHeight(), t = thick.getInt();
        RenderUtils.gradientH(g, 0, 0, w, t, InterfaceModule.accent(), InterfaceModule.accent2());
        RenderUtils.gradientH(g, 0, h - t, w, t, InterfaceModule.accent2(), InterfaceModule.accent());
        RenderUtils.gradientV(g, 0, t, t, h - 2 * t, InterfaceModule.accent(), InterfaceModule.accent2());
        RenderUtils.gradientV(g, w - t, t, t, h - 2 * t, InterfaceModule.accent(), InterfaceModule.accent2());
    }
}
