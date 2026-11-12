package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import net.minecraft.client.player.LocalPlayer;

/** Обычный спринт при ходьбе вперёд: нажимает клавишу спринта локально, без отправки лишних пакетов. */
public final class AutoSprintModule extends Module {
    public AutoSprintModule() {
        super("AutoSprint", "Автоматический спринт при ходьбе вперёд", Category.PLAYER);
    }

    @Override
    public void onTick() {
        LocalPlayer p = mc.player;
        if (p == null || mc.screen != null) return;
        boolean canSprint = mc.options.keyUp.isDown()
                && !p.isShiftKeyDown()
                && !p.isUsingItem()
                && p.getFoodData().getFoodLevel() > 6;
        if (canSprint) mc.options.keySprint.setDown(true);
    }

    @Override
    public void onDisable() {
        mc.options.keySprint.setDown(false);
    }
}
