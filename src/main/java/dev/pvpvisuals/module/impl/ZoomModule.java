package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

/** Зум через изменение FOV (минимум в игре 30). Работает без миксинов. */
public final class ZoomModule extends Module {
    private final ModeSetting mode = add(new ModeSetting("Режим", "Hold", "Hold", "Toggle"));
    private final SliderSetting fov = add(new SliderSetting("FOV", 30, 30, 60, 1));

    private int savedFov = 70;
    private boolean applied;

    public ZoomModule() {
        super("Zoom", "Приближение камеры", Category.VISUALS);
        setKey(GLFW.GLFW_KEY_C);
    }

    @Override
    public boolean isHoldMode() {
        return mode.is("Hold");
    }

    @Override
    public void onEnable() {
        savedFov = mc.options.fov().get();
        applied = true;
        mc.options.fov().set(fov.getInt());
    }

    @Override
    public void onDisable() {
        if (applied) {
            mc.options.fov().set(savedFov);
            applied = false;
        }
    }

    @Override
    public void onTick() {
        if (applied && mc.options.fov().get() != fov.getInt()) {
            mc.options.fov().set(fov.getInt());
        }
    }
}
