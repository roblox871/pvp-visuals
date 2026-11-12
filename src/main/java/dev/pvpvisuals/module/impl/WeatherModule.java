package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ModeSetting;

/** Меняет погоду только на вашем клиенте (на сервере она не меняется). */
public final class WeatherModule extends Module {
    private final ModeSetting weather = add(new ModeSetting("Погода", "Clear", "Clear", "Rain", "Thunder"));

    public WeatherModule() {
        super("Weather", "Клиентская смена погоды", Category.VISUALS);
    }

    @Override
    public void onTick() {
        if (mc.level == null) return;
        float rain = weather.is("Clear") ? 0f : 1f;
        float thunder = weather.is("Thunder") ? 1f : 0f;
        mc.level.setRainLevel(rain);
        mc.level.setThunderLevel(thunder);
    }
}
