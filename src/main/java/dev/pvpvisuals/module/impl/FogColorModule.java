package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.mixin.MixinStatus;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.Notifications;

/** Цвет тумана. Работает через миксин FogRendererMixin. Дистанции тумана здесь не меняются. */
public final class FogColorModule extends Module {
    private static FogColorModule instance;

    private final ColorSetting color = add(new ColorSetting("Цвет тумана", 0xFFB06CFF));
    private final SliderSetting strength = add(new SliderSetting("Сила, %", 70, 0, 100, 1));

    private int ticks;
    private boolean warned;

    public FogColorModule() {
        super("Fog", "Свой цвет тумана", Category.VISUALS);
        instance = this;
    }

    public static boolean isActive() {
        return instance != null && instance.isEnabled();
    }

    public static float strength() {
        return instance == null ? 0f : instance.strength.getFloat() / 100f;
    }

    public static float red() {
        return instance == null ? 0f : ((instance.color.get() >> 16) & 255) / 255f;
    }

    public static float green() {
        return instance == null ? 0f : ((instance.color.get() >> 8) & 255) / 255f;
    }

    public static float blue() {
        return instance == null ? 0f : (instance.color.get() & 255) / 255f;
    }

    @Override
    public void onEnable() {
        ticks = 0;
        warned = false;
    }

    @Override
    public void onTick() {
        if (mc.level == null || warned) return;
        if (++ticks >= 100 && !MixinStatus.fogApplied) {
            warned = true;
            Notifications.warn("Fog: не поддерживается в этой версии");
        }
    }
}
