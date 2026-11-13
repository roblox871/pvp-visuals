package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;

/** Не открывать меню паузы, когда окно теряет фокус (удобно при переключении на Discord). */
public final class NoPauseModule extends Module {
    private boolean saved;

    public NoPauseModule() {
        super("No Pause", "Нет паузы при потере фокуса окна", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        saved = mc.options.pauseOnLostFocus;
        mc.options.pauseOnLostFocus = false;
    }

    @Override
    public void onTick() {
        mc.options.pauseOnLostFocus = false;
    }

    @Override
    public void onDisable() {
        mc.options.pauseOnLostFocus = saved;
    }
}
