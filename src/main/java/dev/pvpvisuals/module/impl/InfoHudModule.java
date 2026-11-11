package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.util.CpsCounter;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class InfoHudModule extends HudModule {
    private final BooleanSetting fps = add(new BooleanSetting("FPS", true));
    private final BooleanSetting cps = add(new BooleanSetting("CPS", true));
    private final BooleanSetting ping = add(new BooleanSetting("Пинг", true));
    private final BooleanSetting bps = add(new BooleanSetting("Скорость", true));
    private final BooleanSetting coords = add(new BooleanSetting("Координаты", true));
    private final BooleanSetting totems = add(new BooleanSetting("Тотемы", true));

    private final List<String[]> rows = new ArrayList<>();
    private double speed;
    private int tick;

    public InfoHudModule() {
        super("Info", "FPS, CPS, пинг, скорость, координаты, тотемы", 6, 28);
    }

    @Override
    public void onTick() {
        LocalPlayer p = mc.player;
        if (p == null) return;

        double dx = p.getX() - p.xo;
        double dz = p.getZ() - p.zo;
        speed += (Math.sqrt(dx * dx + dz * dz) * 20.0 - speed) * 0.3;

        if (tick++ % 5 != 0) return;
        rows.clear();
        if (fps.isOn()) rows.add(new String[]{"FPS", String.valueOf(mc.getFps())});
        if (cps.isOn()) rows.add(new String[]{"CPS", CpsCounter.left() + " | " + CpsCounter.right()});
        if (ping.isOn() && mc.getConnection() != null) {
            PlayerInfo pi = mc.getConnection().getPlayerInfo(p.getUUID());
            rows.add(new String[]{"Пинг", (pi == null ? "-" : String.valueOf(pi.getLatency())) + " ms"});
        }
        if (bps.isOn()) rows.add(new String[]{"BPS", String.format(Locale.ROOT, "%.1f", speed)});
        if (coords.isOn()) {
            rows.add(new String[]{"XYZ", String.format(Locale.ROOT, "%.0f %.0f %.0f", p.getX(), p.getY(), p.getZ())});
        }
        if (totems.isOn()) {
            int n = 0;
            for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
                ItemStack st = p.getInventory().getItem(i);
                if (st.is(Items.TOTEM_OF_UNDYING)) n += st.getCount();
            }
            rows.add(new String[]{"Тотемы", String.valueOf(n)});
        }
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (rows.isEmpty()) {
            width = 60;
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
