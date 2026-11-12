package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.FpsBoost;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ModeSetting;

public final class FpsBoostModule extends Module {
    private final ModeSetting mode = add(new ModeSetting("Режим", "Balanced", "Balanced", "Max"));
    private String applied = "";

    public FpsBoostModule() {
        super("FPS Boost", "Снижает дальность прорисовки, частицы, облака и тени", Category.MISC);
    }

    @Override
    public void onEnable() {
        applied = mode.get();
        FpsBoost.apply(true, mode.is("Max"));
    }

    @Override
    public void onDisable() {
        FpsBoost.apply(false, false);
    }

    @Override
    public void onTick() {
        if (!applied.equals(mode.get())) {
            applied = mode.get();
            FpsBoost.apply(true, mode.is("Max"));
        }
    }
}
