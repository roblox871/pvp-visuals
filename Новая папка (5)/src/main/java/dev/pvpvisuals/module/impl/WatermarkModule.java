package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class WatermarkModule extends HudModule {
    private static final String TITLE = "PvP Visuals";
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final BooleanSetting name = add(new BooleanSetting("Ник", true));
    private final BooleanSetting fps = add(new BooleanSetting("FPS", true));
    private final BooleanSetting ping = add(new BooleanSetting("Пинг", true));
    private final BooleanSetting time = add(new BooleanSetting("Время", false));

    private String rest = "";
    private int tick;

    public WatermarkModule() {
        super("Watermark", "Название мода и краткая информация", 6, 6);
    }

    @Override
    public void onTick() {
        if (tick++ % 10 != 0 || mc.player == null) return;
        StringBuilder sb = new StringBuilder();
        if (name.isOn()) sb.append(" | ").append(mc.player.getName().getString());
        if (fps.isOn()) sb.append(" | ").append(mc.getFps()).append(" fps");
        if (ping.isOn() && mc.getConnection() != null) {
            PlayerInfo pi = mc.getConnection().getPlayerInfo(mc.player.getUUID());
            if (pi != null) sb.append(" | ").append(pi.getLatency()).append(" ms");
        }
        if (time.isOn()) sb.append(" | ").append(LocalTime.now().format(TIME));
        rest = sb.toString();
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        int accent = InterfaceModule.accent();
        int tw = RenderUtils.textWidth(TITLE);
        width = tw + RenderUtils.textWidth(rest) + 12;
        height = 16;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), accent);
        RenderUtils.text(g, TITLE, x + 6, y + 4, accent);
        RenderUtils.text(g, rest, x + 6 + tw, y + 4, RenderUtils.WHITE);
    }
}
