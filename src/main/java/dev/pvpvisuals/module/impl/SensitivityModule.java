package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;

/** Чувствительность мыши в процентах (100% - значение «50%» в ванильных настройках). */
public final class SensitivityModule extends Module {
    private final SliderSetting percent = add(new SliderSetting("Чувствительность, %", 100, 10, 200, 5));
    private double saved = 0.5;

    public SensitivityModule() {
        super("Sensitivity", "Быстрая настройка чувствительности мыши", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        saved = mc.options.sensitivity().get();
    }

    @Override
    public void onTick() {
        double target = percent.getFloat() / 200.0;
        if (Math.abs(mc.options.sensitivity().get() - target) > 1e-6) mc.options.sensitivity().set(target);
    }

    @Override
    public void onDisable() {
        mc.options.sensitivity().set(saved);
    }
}
