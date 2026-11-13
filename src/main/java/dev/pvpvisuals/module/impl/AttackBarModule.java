package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Полоска перезарядки удара под прицелом (та же информация, что и у ванильного индикатора). */
public final class AttackBarModule extends Module {
    public AttackBarModule() {
        super("Attack Bar", "Полоска перезарядки удара под прицелом", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        if (mc.player == null || !mc.options.getCameraType().isFirstPerson()) return;
        float k = Math.max(0f, Math.min(1f, mc.player.getAttackStrengthScale(0f)));
        int w = 30, cx = g.guiWidth() / 2 - w / 2, cy = g.guiHeight() / 2 + 14;
        RenderUtils.roundRect(g, cx, cy, w, 3, 1, 0x90000000);
        int color = k >= 1f ? 0xFF55FF55 : RenderUtils.mix(0xFFFF4D4D, 0xFFFFD54D, k);
        if (k > 0.03f) RenderUtils.roundRect(g, cx, cy, Math.max(2, (int) (w * k)), 3, 1, color);
    }
}
