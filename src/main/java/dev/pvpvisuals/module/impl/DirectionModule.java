package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

import java.util.Locale;

/** Направление взгляда: сторона света и угол. */
public final class DirectionModule extends HudModule {
    private static final String[] NAMES = {"Юг", "Юго-запад", "Запад", "Северо-запад", "Север", "Северо-восток", "Восток", "Юго-восток"};

    public DirectionModule() {
        super("Direction", "Сторона света и угол взгляда", -1, -1);
        width = 96;
        height = 16;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 - width / 2;
        y = 6;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        float yaw = Mth.wrapDegrees(p.getYRot());
        float norm = (yaw % 360f + 360f) % 360f;
        String name = NAMES[Math.round(norm / 45f) % 8];
        String text = name + "  " + String.format(Locale.ROOT, "%.0f", yaw);
        width = RenderUtils.textWidth(text) + 14;
        height = 16;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        RenderUtils.textCentered(g, text, x + width / 2, y + 4, RenderUtils.WHITE);
    }
}
