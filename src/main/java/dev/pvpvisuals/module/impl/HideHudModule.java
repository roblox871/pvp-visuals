package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;

/** Скрывает ванильный интерфейс (как F1). Назначьте клавишу средней кнопкой мыши в ClickGUI. */
public final class HideHudModule extends Module {
    public HideHudModule() {
        super("Hide HUD", "Скрыть интерфейс игры (как F1)", Category.MISC);
    }

    @Override
    public void onEnable() {
        mc.options.hideGui = true;
    }

    @Override
    public void onDisable() {
        mc.options.hideGui = false;
    }
}
