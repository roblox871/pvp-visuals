package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PotionsHudModule extends HudModule {
    private record Row(String name, String time, boolean good) {}

    private final List<Row> rows = new ArrayList<>();
    private int tick;

    public PotionsHudModule() {
        super("Potions HUD", "Активные эффекты и время до конца", 6, 100);
    }

    @Override
    public void onTick() {
        if (mc.player == null || tick++ % 5 != 0) return;
        rows.clear();
        for (MobEffectInstance e : mc.player.getActiveEffects()) {
            String name = e.getEffect().value().getDisplayName().getString() + " " + (e.getAmplifier() + 1);
            String time;
            if (e.isInfiniteDuration()) {
                time = "∞";
            } else {
                int s = e.getDuration() / 20;
                time = (s / 60) + ":" + String.format(Locale.ROOT, "%02d", s % 60);
            }
            rows.add(new Row(name, time, e.getEffect().value().isBeneficial()));
        }
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (rows.isEmpty()) {
            width = 80;
            height = 14;
            return;
        }
        int nw = 0, tw = 0;
        for (Row r : rows) {
            nw = Math.max(nw, RenderUtils.textWidth(r.name()));
            tw = Math.max(tw, RenderUtils.textWidth(r.time()));
        }
        width = nw + tw + 20;
        height = rows.size() * 11 + 6;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        int ty = y + 4;
        for (Row r : rows) {
            RenderUtils.text(g, r.name(), x + 5, ty, r.good() ? RenderUtils.WHITE : 0xFFFF7070);
            RenderUtils.textRight(g, r.time(), x + width - 5, ty, RenderUtils.GRAY);
            ty += 11;
        }
    }
}
