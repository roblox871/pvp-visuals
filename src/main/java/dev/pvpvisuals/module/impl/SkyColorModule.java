package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.mixin.MixinStatus;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.util.Notifications;

/** Свой цвет неба. */
public final class SkyColorModule extends Module {
    private static SkyColorModule instance;

    private final ColorSetting color = add(new ColorSetting("Цвет неба", 0xFF6EA8FF));
    private int ticks;
    private boolean warned;

    public SkyColorModule() {
        super("Sky Color", "Свой цвет неба", Category.VISUALS);
        instance = this;
    }

    /** ARGB цвет неба или 0, если модуль выключен. */
    public static int color() {
        if (instance == null || !instance.isEnabled()) return 0;
        return 0xFF000000 | (instance.color.get() & 0xFFFFFF);
    }

    @Override
    public void onEnable() {
        ticks = 0;
        warned = false;
    }

    @Override
    public void onTick() {
        if (warned || mc.level == null) return;
        if (++ticks >= 100 && !MixinStatus.skyApplied) {
            warned = true;
            Notifications.warn("Sky Color: не поддерживается в этой версии");
        }
    }
}
