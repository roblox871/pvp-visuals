package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.util.Notifications;

/** Короткое приветствие при входе в мир с подсказкой, как открыть меню. */
public final class WelcomeModule extends Module {
    private boolean shown;

    public WelcomeModule() {
        super("Welcome", "Приветствие при входе в мир", Category.MISC);
        this.enabled = true;
    }

    @Override
    public void onTick() {
        if (mc.level == null) {
            shown = false;
            return;
        }
        if (!shown) {
            shown = true;
            Notifications.info("PvP Visuals: Right Shift - меню");
        }
    }
}
