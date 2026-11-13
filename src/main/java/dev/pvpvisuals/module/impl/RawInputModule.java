package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;

/** Сырой ввод мыши: без системного ускорения курсора. */
public final class RawInputModule extends Module {
    private boolean saved;

    public RawInputModule() {
        super("Raw Input", "Сырой ввод мыши без ускорения Windows", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        saved = mc.options.rawMouseInput().get();
        mc.options.rawMouseInput().set(true);
    }

    @Override
    public void onDisable() {
        mc.options.rawMouseInput().set(saved);
    }
}
