package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Анимированная радужная (или в цвет интерфейса) полоска вверху экрана. */
public final class RainbowBarModule extends Module {
    private final BooleanSetting rainbow = add(new BooleanSetting("Радуга", true));
    private final SliderSetting height = add(new SliderSetting("Толщина", 2, 1, 5, 1));
    private final SliderSetting speed = add(new SliderSetting("Скорость", 3, 1, 10, 1));

    public RainbowBarModule() {
        super("Rainbow Bar", "Анимированная полоска вверху экрана", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        int w = g.guiWidth(), h = height.getInt(), seg = 6;
        float t = (System.currentTimeMillis() % 20000L) / 20000f * speed.getFloat();
        for (int x = 0; x < w; x += seg) {
            float k = (x / (float) w + t) % 1f;
            int c = rainbow.isOn() ? RenderUtils.hsb(k, 0.7f, 1f)
                    : RenderUtils.mix(InterfaceModule.accent(), InterfaceModule.accent2(), (float) (0.5 + 0.5 * Math.sin(k * 6.2832)));
            RenderUtils.rect(g, x, 0, Math.min(seg, w - x), h, c);
        }
    }
}
