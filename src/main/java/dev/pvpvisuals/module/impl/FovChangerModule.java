package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;

/** Поле зрения (FOV) ползунком от 30 до 110. */
public final class FovChangerModule extends Module {
    private final SliderSetting fov = add(new SliderSetting("FOV", 90, 30, 110, 1));
    private int saved = 70;

    public FovChangerModule() {
        super("FOV Changer", "Поле зрения от 30 до 110", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        saved = mc.options.fov().get();
    }

    @Override
    public void onTick() {
        if (mc.options.fov().get() != fov.getInt()) mc.options.fov().set(fov.getInt());
    }

    @Override
    public void onDisable() {
        mc.options.fov().set(saved);
    }
}
