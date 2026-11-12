package dev.pvpvisuals.util;

import dev.pvpvisuals.module.impl.InterfaceModule;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/** Всплывающие уведомления о включении и выключении модулей (справа внизу). */
public final class Notifications {
    private Notifications() {}

    private record Toast(String text, String state, boolean on, long time) {}

    private static final long LIFE = 2600;
    private static final List<Toast> TOASTS = new ArrayList<>();

    public static void push(String name, boolean on) {
        show(name, on ? "ВКЛ" : "ВЫКЛ", on);
    }

    /** Предупреждение (красная точка), показывается всегда, даже если обычные уведомления выключены. */
    public static void warn(String text) {
        TOASTS.add(new Toast(text, "!", false, System.currentTimeMillis()));
        if (TOASTS.size() > 5) TOASTS.remove(0);
    }

    /** Сообщение с зелёной точкой, показывается всегда. */
    public static void info(String text) {
        TOASTS.add(new Toast(text, "OK", true, System.currentTimeMillis()));
        if (TOASTS.size() > 5) TOASTS.remove(0);
    }

    private static void show(String text, String state, boolean on) {
        if (!InterfaceModule.notificationsOn()) return;
        TOASTS.add(new Toast(text, state, on, System.currentTimeMillis()));
        if (TOASTS.size() > 5) TOASTS.remove(0);
    }

    public static void render(GuiGraphics g) {
        long now = System.currentTimeMillis();
        TOASTS.removeIf(t -> now - t.time() > LIFE);
        int y = g.guiHeight() - 46;
        int bgAlpha = InterfaceModule.background() >>> 24;

        for (int i = TOASTS.size() - 1; i >= 0; i--) {
            Toast t = TOASTS.get(i);
            long age = now - t.time();
            float in = Math.min(1f, age / 220f);
            float out = Math.min(1f, (LIFE - age) / 300f);
            float a = Math.max(0f, Math.min(in, out));

            String state = t.state();
            int w = RenderUtils.textWidth(t.text() + "  " + state) + 22;
            int h = 18;
            int x = g.guiWidth() - w - 6 + (int) ((1f - in) * (w + 10));

            int bg = RenderUtils.withAlpha(InterfaceModule.background(), Math.max(8, (int) (bgAlpha * a)));
            RenderUtils.panel(g, x, y, w, h, bg, InterfaceModule.accent());

            int textAlpha = Math.max(8, (int) (255 * a));
            int dot = t.on() ? 0xFF55FF55 : 0xFFFF5555;
            RenderUtils.roundRect(g, x + 7, y + 7, 4, 4, 2, RenderUtils.withAlpha(dot, textAlpha));
            RenderUtils.text(g, t.text(), x + 15, y + 5, RenderUtils.withAlpha(0xFFFFFF, textAlpha));
            RenderUtils.textRight(g, state, x + w - 6, y + 5, RenderUtils.withAlpha(dot, textAlpha));
            y -= h + 4;
        }
    }
}
