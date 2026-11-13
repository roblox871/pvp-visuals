package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Короткая цветная вспышка экрана, когда вы получаете урон. */
public final class HurtFlashModule extends Module {
    private final ColorSetting color = add(new ColorSetting("Цвет", 0xFFFF2A2A));
    private final SliderSetting strength = add(new SliderSetting("Сила, %", 30, 5, 80, 1));

    public HurtFlashModule() {
        super("Hurt Flash", "Вспышка экрана при получении урона", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        if (mc.player == null || mc.player.hurtTime <= 0) return;
        float k = Math.min(1f, mc.player.hurtTime / 10f);
        g.fill(0, 0, g.guiWidth(), g.guiHeight(), RenderUtils.withAlpha(color.get(), (int) (2.55 * strength.getFloat() * k)));
    }
}
