package dev.pvpvisuals.util;

import dev.pvpvisuals.module.impl.InterfaceModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.awt.Color;

/**
 * 2D-рендер поверх GuiGraphics. Начиная с 1.21.6 старый путь RenderSystem.setShader + Tesselator убран
 * (рендер идёт через RenderPipeline), поэтому весь 2D-интерфейс рисуется через GuiGraphics: он стабилен между версиями.
 * Цвета везде в формате ARGB.
 */
public final class RenderUtils {
    private RenderUtils() {}

    public static final int WHITE = 0xFFFFFFFF;
    public static final int GRAY = 0xFF9A9AA8;
    public static final int DARK = 0xFF2A2A32;

    public static void rect(GuiGraphics g, int x, int y, int w, int h, int color) {
        if (w <= 0 || h <= 0) return;
        g.fill(x, y, x + w, y + h, color);
    }

    public static void gradientV(GuiGraphics g, int x, int y, int w, int h, int top, int bottom) {
        if (w <= 0 || h <= 0) return;
        g.fillGradient(x, y, x + w, y + h, top, bottom);
    }

    /** Горизонтальный градиент: рисуется не более чем 32 полосами, чтобы не нагружать кадр. */
    public static void gradientH(GuiGraphics g, int x, int y, int w, int h, int left, int right) {
        if (w <= 0 || h <= 0) return;
        int steps = Math.min(w, 32);
        for (int i = 0; i < steps; i++) {
            int x1 = x + i * w / steps;
            int x2 = x + (i + 1) * w / steps;
            if (x2 <= x1) continue;
            g.fill(x1, y, x2, y + h, mix(left, right, steps == 1 ? 0f : i / (float) (steps - 1)));
        }
    }

    public static void outline(GuiGraphics g, int x, int y, int w, int h, int color) {
        rect(g, x, y, w, 1, color);
        rect(g, x, y + h - 1, w, 1, color);
        rect(g, x, y + 1, 1, h - 2, color);
        rect(g, x + w - 1, y + 1, 1, h - 2, color);
    }

    /** Скруглённый прямоугольник: радиус строится построчно, без текстур и шейдеров. */
    public static void roundRect(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            rect(g, x, y, w, h, color);
            return;
        }
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = r - (int) Math.round(Math.sqrt(r * r - dy * dy));
            rect(g, x + inset, y + i, w - inset * 2, 1, color);
            rect(g, x + inset, y + h - 1 - i, w - inset * 2, 1, color);
        }
        rect(g, x, y + r, w, h - r * 2, color);
    }

    /** Панель в выбранном стиле интерфейса: тень, скругление, градиентная линия или боковая полоса. */
    public static void panel(GuiGraphics g, int x, int y, int w, int h, int background, int accent) {
        int r = InterfaceModule.radius();
        if (InterfaceModule.shadow()) {
            roundRect(g, x - 1, y - 1, w + 2, h + 2, r + 1, 0x30000000);
            roundRect(g, x - 2, y - 2, w + 4, h + 4, r + 2, 0x14000000);
        }
        roundRect(g, x, y, w, h, r, background);
        int inner = Math.max(0, w - 2 * r);
        switch (InterfaceModule.styleName()) {
            case "Minimal" -> rect(g, x, y + r, 2, Math.max(0, h - 2 * r), accent);
            case "Solid" -> gradientH(g, x + r, y, inner, 2, accent, InterfaceModule.accent2());
            case "Neon" -> {
                int c1 = withAlpha(accent, 200), c2 = withAlpha(InterfaceModule.accent2(), 200);
                gradientH(g, x + r, y, inner, 1, c1, c2);
                gradientH(g, x + r, y + h - 1, inner, 1, c2, c1);
                gradientV(g, x, y + r, 1, Math.max(0, h - 2 * r), c1, c2);
                gradientV(g, x + w - 1, y + r, 1, Math.max(0, h - 2 * r), c1, c2);
                gradientV(g, x + 1, y + 1, Math.max(0, w - 2), Math.max(0, h / 2), withAlpha(accent, 40), 0x00000000);
            }
            default -> {
                gradientH(g, x + r, y, inner, 1, accent, InterfaceModule.accent2());
                // мягкий «глянец» в верхней половине панели и тонкая подсветка снизу
                gradientV(g, x + 1, y + 1, Math.max(0, w - 2), Math.max(0, h / 2), 0x1CFFFFFF, 0x00FFFFFF);
                gradientH(g, x + r, y + h - 1, inner, 1, withAlpha(accent, 70), withAlpha(InterfaceModule.accent2(), 70));
            }
        }
    }

    public static void text(GuiGraphics g, String s, int x, int y, int color) {
        g.drawString(Minecraft.getInstance().font, s, x, y, color, InterfaceModule.textShadow());
    }

    public static void textRight(GuiGraphics g, String s, int right, int y, int color) {
        text(g, s, right - textWidth(s), y, color);
    }

    public static void textCentered(GuiGraphics g, String s, int cx, int y, int color) {
        text(g, s, cx - textWidth(s) / 2, y, color);
    }

    public static int textWidth(String s) {
        return Minecraft.getInstance().font.width(s);
    }

    public static void item(GuiGraphics g, ItemStack stack, int x, int y) {
        g.renderItem(stack, x, y);
    }

    public static boolean hover(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    public static int withAlpha(int color, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (color & 0xFFFFFF);
    }

    public static int mix(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255, aa = (a >>> 24) & 255;
        int br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255, ba = (b >>> 24) & 255;
        return ((int) (aa + (ba - aa) * t) << 24)
                | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8)
                | (int) (ab + (bb - ab) * t);
    }

    public static int hsb(float h, float s, float b) {
        return 0xFF000000 | (Color.HSBtoRGB(h, s, b) & 0xFFFFFF);
    }
}
