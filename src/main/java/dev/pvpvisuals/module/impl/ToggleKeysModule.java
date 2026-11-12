package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;

/** Включает встроенные режимы «Переключать приседание» и «Переключать спринт» (нажал один раз, и режим держится). */
public final class ToggleKeysModule extends Module {
    private final BooleanSetting sneak = add(new BooleanSetting("Toggle Sneak", true));
    private final BooleanSetting sprint = add(new BooleanSetting("Toggle Sprint", false));

    private boolean savedSneak, savedSprint;
    private boolean saved;

    public ToggleKeysModule() {
        super("Toggle Keys", "Переключаемые приседание и спринт", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        savedSneak = mc.options.toggleCrouch().get();
        savedSprint = mc.options.toggleSprint().get();
        saved = true;
    }

    @Override
    public void onTick() {
        if (!saved) return;
        mc.options.toggleCrouch().set(sneak.isOn() ? Boolean.TRUE : savedSneak);
        mc.options.toggleSprint().set(sprint.isOn() ? Boolean.TRUE : savedSprint);
    }

    @Override
    public void onDisable() {
        if (!saved) return;
        mc.options.toggleCrouch().set(savedSneak);
        mc.options.toggleSprint().set(savedSprint);
        saved = false;
    }
}
