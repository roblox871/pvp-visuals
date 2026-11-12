package dev.pvpvisuals.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * Определяет ваши удары: у существа под прицелом появляется «красная вспышка» (hurtTime растёт),
 * и вы только что кликнули левой кнопкой. Используется модулями с эффектами удара.
 */
public final class HitTracker {
    private HitTracker() {}

    private static LivingEntity target;
    private static int lastHurt;
    private static int hits;
    private static LivingEntity lastHitEntity;
    private static long lastHitTime;

    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        LivingEntity t = mc.crosshairPickEntity instanceof LivingEntity le && le != p ? le : null;
        if (t == null) return;
        if (t != target) {
            target = t;
            lastHurt = t.hurtTime;
            return;
        }
        if (t.hurtTime > lastHurt && CpsCounter.sinceLeftClick() < 400) {
            hits++;
            lastHitEntity = t;
            lastHitTime = System.currentTimeMillis();
        }
        lastHurt = t.hurtTime;
    }

    /** Счётчик ударов: растёт на 1 при каждом ударе. */
    public static int hits() {
        return hits;
    }

    public static LivingEntity lastEntity() {
        return lastHitEntity;
    }

    public static long lastHitTime() {
        return lastHitTime;
    }
}
