package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.mixin.MixinStatus;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.Notifications;

/** Время суток только на вашем экране (сервер и другие игроки не затрагиваются). */
public final class TimeChangerModule extends Module {
    private static TimeChangerModule instance;

    private final SliderSetting hour = add(new SliderSetting("Время, часы", 12, 0, 24, 0.5));
    private int ticks;
    private boolean warned;

    public TimeChangerModule() {
        super("Time Changer", "Своё время суток на экране", Category.VISUALS);
        instance = this;
    }

    /** Значение для getDayTime() или -1, если модуль выключен. В игре 0 ч игровых суток = 18:00, 6:00 = 0 тиков. */
    public static long fixedTime() {
        if (instance == null || !instance.isEnabled()) return -1;
        double h = instance.hour.getFloat();
        return Math.round(((h - 6.0 + 24.0) % 24.0) * 1000.0);
    }

    @Override
    public void onEnable() {
        ticks = 0;
        warned = false;
    }

    @Override
    public void onTick() {
        if (warned || mc.level == null) return;
        if (++ticks >= 100 && !MixinStatus.timeApplied) {
            warned = true;
            Notifications.warn("Time Changer: не поддерживается в этой версии");
        }
    }
}
