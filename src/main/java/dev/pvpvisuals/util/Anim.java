package dev.pvpvisuals.util;

/** Покадровая анимация: значения плавно приближаются к цели независимо от FPS. */
public final class Anim {
    private Anim() {}

    private static long last = System.nanoTime();
    private static float dt = 0.016f;

    /** Вызывается один раз за кадр в начале отрисовки HUD. */
    public static void frame() {
        long now = System.nanoTime();
        dt = Math.min(0.1f, (now - last) / 1_000_000_000f);
        last = now;
    }

    public static float approach(float current, float target, float speed) {
        float k = 1f - (float) Math.exp(-speed * dt);
        return current + (target - current) * k;
    }
}
