package dev.pvpvisuals.mixin;

/**
 * Показывает, сработал ли миксин. Если метод игры в этой версии называется иначе, миксин молча не применяется
 * (defaultRequire = 0, required = false), и модуль сообщает игроку, что функция не поддерживается.
 */
public final class MixinStatus {
    private MixinStatus() {}

    public static volatile boolean aspectApplied;
    public static volatile boolean fogApplied;
    public static volatile boolean fireApplied;
    public static volatile boolean itemApplied;
    public static volatile boolean swingApplied;
    public static volatile boolean timeApplied;
    public static volatile boolean skyApplied;
    public static volatile boolean turnApplied;
    public static volatile boolean cameraApplied;
    public static volatile boolean popApplied;
}
