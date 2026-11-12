package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Реальное время и время игры в текущем запуске. */
public final class SessionModule extends HudModule {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final long START = System.currentTimeMillis();

    public SessionModule() {
        super("Session", "Время на часах и время игры", 6, 270);
        width = 96;
        height = 28;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        long sec = (System.currentTimeMillis() - START) / 1000;
        String played = String.format(Locale.ROOT, "%d:%02d:%02d", sec / 3600, (sec / 60) % 60, sec % 60);
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        RenderUtils.text(g, "Время", x + 5, y + 4, RenderUtils.GRAY);
        RenderUtils.textRight(g, LocalTime.now().format(CLOCK), x + width - 5, y + 4, RenderUtils.WHITE);
        RenderUtils.text(g, "В игре", x + 5, y + 15, RenderUtils.GRAY);
        RenderUtils.textRight(g, played, x + width - 5, y + 15, RenderUtils.WHITE);
    }
}
