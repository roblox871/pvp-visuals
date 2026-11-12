package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.SliderSetting;
import net.minecraft.client.gui.screens.DeathScreen;

/** Сам нажимает «Возродиться» после смерти. */
public final class AutoRespawnModule extends Module {
    private final SliderSetting delay = add(new SliderSetting("Задержка, тики", 10, 0, 60, 1));
    private int waited;

    public AutoRespawnModule() {
        super("Auto Respawn", "Автоматическое возрождение после смерти", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.screen instanceof DeathScreen) {
            if (++waited >= delay.getInt()) {
                waited = 0;
                mc.player.respawn();
            }
        } else {
            waited = 0;
        }
    }
}
