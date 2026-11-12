package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/** Адрес сервера и число игроков в сети. */
public final class ServerInfoModule extends HudModule {
    private final List<String[]> rows = new ArrayList<>();
    private int tick;

    public ServerInfoModule() {
        super("Server Info", "Адрес сервера и онлайн", 6, 170);
    }

    @Override
    public void onTick() {
        if (tick++ % 20 != 0) return;
        rows.clear();
        var server = mc.getCurrentServer();
        rows.add(new String[]{"Сервер", server == null ? "Одиночная игра" : server.ip});
        if (mc.getConnection() != null) {
            rows.add(new String[]{"Онлайн", String.valueOf(mc.getConnection().getOnlinePlayers().size())});
        }
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (rows.isEmpty()) {
            width = 80;
            height = 14;
            return;
        }
        int lw = 0, vw = 0;
        for (String[] r : rows) {
            lw = Math.max(lw, RenderUtils.textWidth(r[0]));
            vw = Math.max(vw, RenderUtils.textWidth(r[1]));
        }
        width = lw + vw + 18;
        height = rows.size() * 11 + 6;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        int ty = y + 4;
        for (String[] r : rows) {
            RenderUtils.text(g, r[0], x + 5, ty, RenderUtils.GRAY);
            RenderUtils.textRight(g, r[1], x + width - 5, ty, RenderUtils.WHITE);
            ty += 11;
        }
    }
}
