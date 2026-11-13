package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Красное пульсирующее свечение по краям экрана, когда мало здоровья. */
public final class LowHealthVignetteModule extends Module {
    private final SliderSetting threshold = add(new SliderSetting("Порог HP", 8, 2, 20, 1));
    private final SliderSetting size = add(new SliderSetting("Размер, px", 40, 10, 120, 5));
    private final BooleanSetting pulse = add(new BooleanSetting("Пульсация", true));

    public LowHealthVignetteModule() {
        super("Low HP Vignette", "Красные края экрана при низком здоровье", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        if (mc.player == null) return;
        float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        float max = threshold.getFloat();
        if (hp >= max) return;

        float level = 1f - Math.max(0f, hp) / max;
        float wave = pulse.isOn() ? 0.65f + 0.35f * (float) Math.sin(System.currentTimeMillis() / 180.0) : 1f;
        int alpha = (int) (170 * level * wave);
        int edge = RenderUtils.withAlpha(0xFFFF1A1A, Math.max(0, Math.min(255, alpha)));
        int none = 0x00FF1A1A;

        int w = g.guiWidth(), h = g.guiHeight(), s = size.getInt();
        RenderUtils.gradientV(g, 0, 0, w, s, edge, none);
        RenderUtils.gradientV(g, 0, h - s, w, s, none, edge);
        RenderUtils.gradientH(g, 0, 0, s, h, edge, none);
        RenderUtils.gradientH(g, w - s, 0, s, h, none, edge);
    }
}
