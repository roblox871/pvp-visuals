package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;

/** Плавный поворот камеры через встроенный кинематографический режим игры. */
public final class SmoothCameraModule extends Module {
    private boolean previous;

    public SmoothCameraModule() {
        super("Smooth Camera", "Плавность при повороте камеры", Category.VISUALS);
    }

    @Override
    public void onEnable() {
        previous = mc.options.smoothCamera;
        mc.options.smoothCamera = true;
    }

    @Override
    public void onDisable() {
        mc.options.smoothCamera = previous;
    }

    @Override
    public void onTick() {
        if (!mc.options.smoothCamera) mc.options.smoothCamera = true;
    }
}
