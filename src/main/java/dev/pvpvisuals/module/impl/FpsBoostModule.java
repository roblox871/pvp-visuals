package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.FpsBoost;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;

public final class FpsBoostModule extends Module {
    public FpsBoostModule() {
        super("FPS Boost", "Снижает дальность прорисовки, частицы и облака", Category.MISC);
    }

    @Override
    public void onEnable() {
        FpsBoost.apply(true);
    }

    @Override
    public void onDisable() {
        FpsBoost.apply(false);
    }
}
