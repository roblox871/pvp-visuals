package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;

public final class TargetHudModule extends HudModule {
    private final ModeSetting style = add(new ModeSetting("Стиль", "Compact", "Compact", "Wide"));
    private final BooleanSetting showHealth = add(new BooleanSetting("Здоровье числом", true));
    private final SliderSetting hold = add(new SliderSetting("Показывать, сек", 3, 1, 10, 1));

    private LivingEntity target;
    private long seenAt;
    private float shownHealth;

    public TargetHudModule() {
        super("TargetHUD", "Здоровье цели, на которую вы смотрите", -1, -1);
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 + 14;
        y = sh / 2 + 14;
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        long now = System.currentTimeMillis();
        if (mc.crosshairPickEntity instanceof LivingEntity le && le != mc.player) {
            if (le != target) shownHealth = le.getHealth();
            target = le;
            seenAt = now;
        } else if (now - seenAt > hold.getInt() * 1000L) {
            target = null;
        }
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (target == null || !target.isAlive()) {
            width = 110;
            height = 30;
            return;
        }
        float hp = target.getHealth();
        float max = Math.max(1f, target.getMaxHealth());
        shownHealth += (hp - shownHealth) * Math.min(1f, delta.getRealtimeDeltaTicks() * 0.25f);

        String name = target.getName().getString();
        String hpText = String.format(Locale.ROOT, "%.1f", hp);
        int base = style.is("Wide") ? 150 : 110;
        width = Math.max(base, RenderUtils.textWidth(name) + RenderUtils.textWidth(hpText) + 22);
        height = 30;

        int accent = InterfaceModule.accent();
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), accent);
        RenderUtils.text(g, name, x + 6, y + 5, RenderUtils.WHITE);
        if (showHealth.isOn()) RenderUtils.textRight(g, hpText, x + width - 6, y + 5, RenderUtils.GRAY);

        int bx = x + 6, by = y + 19, bw = width - 12;
        RenderUtils.rect(g, bx, by, bw, 4, RenderUtils.DARK);
        int trail = Math.round(bw * clamp01(shownHealth / max));
        int real = Math.round(bw * clamp01(hp / max));
        RenderUtils.rect(g, bx, by, trail, 4, 0xFF8A8A96);
        float ratio = hp / max;
        int color = ratio > 0.5f ? 0xFF55FF55 : ratio > 0.25f ? 0xFFFFFF55 : 0xFFFF5555;
        RenderUtils.rect(g, bx, by, real, 4, color);
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}
