package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.CpsCounter;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.Locale;

/** Счётчик комбо (удары подряд) и дальности последнего удара. */
public final class ComboModule extends HudModule {
    private int combo;
    private double reach;
    private long lastHit;

    private LivingEntity last;
    private int lastHurt;
    private int prevOwnHurt;

    public ComboModule() {
        super("Combo", "Комбо и дальность последнего удара", -1, -1);
        width = 84;
        height = 28;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 - width / 2;
        y = sh / 2 + 40;
    }

    @Override
    public void onTick() {
        LocalPlayer p = mc.player;
        if (p == null) return;
        long now = System.currentTimeMillis();

        LivingEntity t = mc.crosshairPickEntity instanceof LivingEntity le && le != p ? le : null;
        if (t != null) {
            if (t != last) {
                last = t;
                lastHurt = t.hurtTime;
            } else {
                if (t.hurtTime > lastHurt && CpsCounter.left() > 0) {
                    combo++;
                    lastHit = now;
                    reach = Math.sqrt(t.getBoundingBox().distanceToSqr(p.getEyePosition()));
                }
                lastHurt = t.hurtTime;
            }
        }

        if (p.hurtTime > 0 && prevOwnHurt == 0) combo = 0;
        prevOwnHurt = p.hurtTime;
        if (combo > 0 && now - lastHit > 3000) combo = 0;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        RenderUtils.text(g, "Комбо", x + 5, y + 4, RenderUtils.GRAY);
        RenderUtils.textRight(g, String.valueOf(combo), x + width - 5, y + 4, RenderUtils.WHITE);
        RenderUtils.text(g, "Дальность", x + 5, y + 15, RenderUtils.GRAY);
        RenderUtils.textRight(g, String.format(Locale.ROOT, "%.2f", reach), x + width - 5, y + 15, RenderUtils.WHITE);
    }
}
