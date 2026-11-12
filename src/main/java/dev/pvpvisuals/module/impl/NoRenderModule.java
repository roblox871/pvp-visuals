package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.SliderSetting;

/** Отключает раздражающие визуальные эффекты через настройки игры. Прежние значения возвращаются при выключении. */
public final class NoRenderModule extends Module {
    private final SliderSetting hurtCam = add(new SliderSetting("Тряска при уроне, %", 0, 0, 100, 5));
    private final BooleanSetting bobbing = add(new BooleanSetting("Покачивание камеры", true));
    private final BooleanSetting fovEffects = add(new BooleanSetting("Изменение FOV при беге", true));
    private final BooleanSetting distortion = add(new BooleanSetting("Искажение (тошнота, портал)", true));
    private final BooleanSetting darkness = add(new BooleanSetting("Пульсация тьмы", true));

    private double savedTilt, savedFov, savedDistortion, savedDarkness;
    private boolean savedBob;
    private boolean saved;

    public NoRenderModule() {
        super("NoRender", "Отключение тряски, покачивания и искажений", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        savedTilt = mc.options.damageTiltStrength().get();
        savedBob = mc.options.bobView().get();
        savedFov = mc.options.fovEffectScale().get();
        savedDistortion = mc.options.screenEffectScale().get();
        savedDarkness = mc.options.darknessEffectScale().get();
        saved = true;
    }

    @Override
    public void onTick() {
        if (!saved) return;
        mc.options.damageTiltStrength().set(savedTilt * hurtCam.get() / 100.0);
        mc.options.bobView().set(bobbing.isOn() ? Boolean.FALSE : savedBob);
        mc.options.fovEffectScale().set(fovEffects.isOn() ? 0.0 : savedFov);
        mc.options.screenEffectScale().set(distortion.isOn() ? 0.0 : savedDistortion);
        mc.options.darknessEffectScale().set(darkness.isOn() ? 0.0 : savedDarkness);
    }

    @Override
    public void onDisable() {
        if (!saved) return;
        mc.options.damageTiltStrength().set(savedTilt);
        mc.options.bobView().set(savedBob);
        mc.options.fovEffectScale().set(savedFov);
        mc.options.screenEffectScale().set(savedDistortion);
        mc.options.darknessEffectScale().set(savedDarkness);
        saved = false;
    }
}
