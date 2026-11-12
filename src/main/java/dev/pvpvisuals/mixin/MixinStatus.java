package dev.pvpvisuals.mixin;

/**
 * Показывает, сработал ли миксин. Если метод игры в этой версии называется иначе, миксин молча не применяется
 * (defaultRequire = 0), и модуль сообщает игроку, что функция не поддерживается.
 */
public final class MixinStatus {
    private MixinStatus() {}

    public static volatile boolean aspectApplied;
    public static volatile boolean fogApplied;
}
