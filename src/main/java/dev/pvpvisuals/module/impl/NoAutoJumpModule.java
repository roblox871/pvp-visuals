package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;

/** Выключает автоматический прыжок на блок. */
public final class NoAutoJumpModule extends Module {
    private boolean saved;

    public NoAutoJumpModule() {
        super("No Auto Jump", "Отключает автопрыжок", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        saved = mc.options.autoJump().get();
        mc.options.autoJump().set(false);
    }

    @Override
    public void onDisable() {
        mc.options.autoJump().set(saved);
    }
}
