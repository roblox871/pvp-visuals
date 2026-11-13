package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/** Полоска-компас вверху экрана. */
public final class CompassModule extends Module {
    private static final String[] NAMES = {"Ю", "ЮЗ", "З", "СЗ", "С", "СВ", "В", "ЮВ"};

    public CompassModule() {
        super("Compass", "Компас вверху экрана", Category.HUD);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        if (mc.player == null) return;
        int cx = g.guiWidth() / 2, y = 4, half = 100;
        float yaw = mc.player.getYRot();
        RenderUtils.panel(g, cx - half, y, half * 2, 14, InterfaceModule.background(), InterfaceModule.accent());
        for (int i = 0; i < 8; i++) {
            float diff = ((i * 45f - yaw) % 360f + 540f) % 360f - 180f;
            if (Math.abs(diff) > 90f) continue;
            int px = cx + (int) (diff / 90f * (half - 12));
            int alpha = (int) (255 * (1f - Math.abs(diff) / 100f));
            boolean main = i % 2 == 0;
            RenderUtils.textCentered(g, NAMES[i], px, y + 3,
                    RenderUtils.withAlpha(main ? InterfaceModule.accent() : RenderUtils.WHITE, alpha));
        }
        RenderUtils.rect(g, cx, y + 12, 1, 4, RenderUtils.WHITE);
    }
}
