package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Цветной светофильтр на весь экран. */
public final class ScreenTintModule extends Module {
    private final ColorSetting color = add(new ColorSetting("Цвет", 0xFF8A5CFF));
    private final SliderSetting alpha = add(new SliderSetting("Прозрачность, %", 15, 2, 60, 1));

    public ScreenTintModule() {
        super("Screen Tint", "Цветной фильтр на экран", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        g.fill(0, 0, g.guiWidth(), g.guiHeight(), RenderUtils.withAlpha(color.get(), (int) (2.55 * alpha.getFloat())));
    }
}
