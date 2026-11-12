package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.util.Projection;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/** Всплывающие числа урона над целью, на которую вы смотрите. Поднимаются вверх и гаснут. */
public final class DamageIndicatorsModule extends Module {
    private record Popup(double x, double y, double z, float amount, long time) {}

    private static final long LIFE = 1000;

    private final List<Popup> popups = new ArrayList<>();
    private LivingEntity tracked;
    private float lastHealth;

    public DamageIndicatorsModule() {
        super("Damage Indicators", "Числа урона над целью", Category.VISUALS);
    }

    @Override
    public void onTick() {
        LocalPlayer p = mc.player;
        if (p == null) return;
        long now = System.currentTimeMillis();
        popups.removeIf(pp -> now - pp.time() > LIFE);

        LivingEntity t = mc.crosshairPickEntity instanceof LivingEntity le && le != p ? le : null;
        if (t == null) return;
        if (t != tracked) {
            tracked = t;
            lastHealth = t.getHealth();
            return;
        }
        float hp = t.getHealth();
        if (hp < lastHealth - 0.01f) {
            double jitter = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.8;
            popups.add(new Popup(t.getX() + jitter, t.getY() + t.getBbHeight() + 0.1, t.getZ() + jitter, lastHealth - hp, now));
        }
        lastHealth = hp;
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer me = mc.player;
        if (me == null || popups.isEmpty() || !mc.options.getCameraType().isFirstPerson()) return;

        Projection proj = Projection.of(me, delta.getGameTimeDeltaPartialTick(false), g.guiWidth(), g.guiHeight());
        long now = System.currentTimeMillis();
        for (Popup pp : popups) {
            float f = Math.min(1f, (now - pp.time()) / (float) LIFE);
            int[] s = proj.toScreen(pp.x(), pp.y() + f * 0.9, pp.z());
            if (s == null) continue;
            int alpha = Math.max(8, (int) (255 * (1f - f * f)));
            int color = pp.amount() >= 6f ? 0xFFFFC74D : 0xFFFF5C5C;
            String text = "-" + String.format(Locale.ROOT, "%.1f", pp.amount());
            RenderUtils.textCentered(g, text, s[0], s[1], RenderUtils.withAlpha(color, alpha));
        }
    }
}
