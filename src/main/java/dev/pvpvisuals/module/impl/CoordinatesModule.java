package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

import java.util.Locale;

/** Координаты XYZ и скорость в блоках в секунду. */
public final class CoordinatesModule extends HudModule {
    private double lastX, lastZ;
    private double speed;
    private long lastTime;

    public CoordinatesModule() {
        super("Coordinates", "Координаты и скорость", 6, 300);
        width = 110;
        height = 40;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null) return;

        long now = System.currentTimeMillis();
        if (now - lastTime >= 250) {
            double dt = (now - lastTime) / 1000.0;
            double dx = p.getX() - lastX, dz = p.getZ() - lastZ;
            double v = Math.sqrt(dx * dx + dz * dz) / dt;
            speed = lastTime == 0 || v > 200 ? 0 : speed * 0.5 + v * 0.5;
            lastX = p.getX();
            lastZ = p.getZ();
            lastTime = now;
        }

        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        RenderUtils.text(g, "XYZ", x + 5, y + 4, RenderUtils.GRAY);
        RenderUtils.textRight(g, String.format(Locale.ROOT, "%.1f", p.getX()), x + width - 5, y + 4, RenderUtils.WHITE);
        RenderUtils.textRight(g, String.format(Locale.ROOT, "%.1f", p.getY()), x + width - 5, y + 15, RenderUtils.WHITE);
        RenderUtils.textRight(g, String.format(Locale.ROOT, "%.1f", p.getZ()), x + width - 5, y + 26, RenderUtils.WHITE);
        RenderUtils.text(g, String.format(Locale.ROOT, "%.1f б/с", speed), x + 5, y + 15, InterfaceModule.accent2());
    }
}
