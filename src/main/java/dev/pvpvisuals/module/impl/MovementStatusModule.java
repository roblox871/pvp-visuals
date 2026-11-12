package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;

/** Индикатор спринта и приседания. Пометка «toggle» показывает, что включён режим переключения. */
public final class MovementStatusModule extends HudModule {
    public MovementStatusModule() {
        super("Movement Status", "Статус спринта и приседания", -1, -1);
        width = 110;
        height = 28;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 - width / 2;
        y = sh - 112;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        boolean toggleSprint = mc.options.toggleSprint().get();
        boolean toggleSneak = mc.options.toggleCrouch().get();

        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        row(g, "Спринт", p.isSprinting(), toggleSprint, y + 4);
        row(g, "Приседание", p.isShiftKeyDown(), toggleSneak, y + 15);
    }

    private void row(GuiGraphics g, String label, boolean on, boolean toggle, int ty) {
        RenderUtils.text(g, label, x + 5, ty, RenderUtils.GRAY);
        String value = (on ? "ВКЛ" : "ВЫКЛ") + (toggle ? " [toggle]" : "");
        RenderUtils.textRight(g, value, x + width - 5, ty, on ? 0xFF55FF55 : RenderUtils.WHITE);
    }
}
