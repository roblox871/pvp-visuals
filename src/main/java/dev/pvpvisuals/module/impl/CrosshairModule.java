package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.HitTracker;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

/** Свой прицел вместо ванильного: стили, цвет, расхождение при ударе, красный цвет на цели, хитмаркер и полоска кулдауна. */
public final class CrosshairModule extends Module {
    private static CrosshairModule instance;

    private final ModeSetting style = add(new ModeSetting("Стиль", "Cross", "Cross", "Dot", "Circle", "Cross+Dot"));
    private final SliderSetting size = add(new SliderSetting("Размер", 5, 1, 14, 1));
    private final SliderSetting gap = add(new SliderSetting("Просвет", 3, 0, 10, 1));
    private final SliderSetting thickness = add(new SliderSetting("Толщина", 1, 1, 4, 1));
    private final ModeSetting colorMode = add(new ModeSetting("Цвет", "White", "White", "Accent", "Custom"));
    private final ColorSetting custom = add(new ColorSetting("Свой цвет", 0xFF55FFAA));
    private final BooleanSetting outline = add(new BooleanSetting("Обводка", true));
    private final BooleanSetting dynamic = add(new BooleanSetting("Расхождение при кулдауне", true));
    private final BooleanSetting targetColor = add(new BooleanSetting("Красный на цели", true));
    private final BooleanSetting hitMarker = add(new BooleanSetting("Хитмаркер", true));
    private final BooleanSetting cooldownBar = add(new BooleanSetting("Полоска кулдауна", false));

    private int seenHits;
    private long markerUntil;

    public CrosshairModule() {
        super("Crosshair", "Свой прицел", Category.VISUALS);
        instance = this;
    }

    /** Нужно ли скрыть ванильный прицел. */
    public static boolean isActive() {
        return instance != null && instance.isEnabled();
    }

    @Override
    public void onTick() {
        int h = HitTracker.hits();
        if (h != seenHits) {
            seenHits = h;
            markerUntil = System.currentTimeMillis() + 180;
        }
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null || !mc.options.getCameraType().isFirstPerson()) return;

        int cx = g.guiWidth() / 2, cy = g.guiHeight() / 2;
        float cooldown = Math.max(0f, Math.min(1f, p.getAttackStrengthScale(0f)));

        int color = switch (colorMode.get()) {
            case "Accent" -> InterfaceModule.accent();
            case "Custom" -> custom.get();
            default -> RenderUtils.WHITE;
        };
        if (targetColor.isOn() && mc.crosshairPickEntity instanceof LivingEntity) color = 0xFFFF4D4D;

        int t = thickness.getInt();
        int s = size.getInt();
        int gp = gap.getInt() + (dynamic.isOn() ? Math.round((1f - cooldown) * 5f) : 0);
        boolean drawCross = style.is("Cross") || style.is("Cross+Dot");

        if (drawCross) {
            arm(g, cx - gp - s, cy - t / 2, s, t, color);
            arm(g, cx + gp + 1, cy - t / 2, s, t, color);
            arm(g, cx - t / 2, cy - gp - s, t, s, color);
            arm(g, cx - t / 2, cy + gp + 1, t, s, color);
        }
        if (style.is("Dot") || style.is("Cross+Dot")) {
            arm(g, cx - t / 2, cy - t / 2, Math.max(2, t), Math.max(2, t), color);
        }
        if (style.is("Circle")) {
            int r = gp + s;
            for (int i = 0; i < 24; i++) {
                double a = i * Math.PI / 12.0;
                arm(g, cx + (int) Math.round(Math.cos(a) * r) - t / 2, cy + (int) Math.round(Math.sin(a) * r) - t / 2, t, t, color);
            }
        }

        if (hitMarker.isOn() && System.currentTimeMillis() < markerUntil) {
            int d = gp + s + 3;
            for (int i = 2; i <= 5; i++) {
                RenderUtils.rect(g, cx - d + i, cy - d + i, 2, 2, 0xFFFF3333);
                RenderUtils.rect(g, cx + d - i - 1, cy - d + i, 2, 2, 0xFFFF3333);
                RenderUtils.rect(g, cx - d + i, cy + d - i - 1, 2, 2, 0xFFFF3333);
                RenderUtils.rect(g, cx + d - i - 1, cy + d - i - 1, 2, 2, 0xFFFF3333);
            }
        }

        if (cooldownBar.isOn() && cooldown < 1f) {
            int bw = 20, by = cy + gp + s + 6;
            RenderUtils.rect(g, cx - bw / 2, by, bw, 2, RenderUtils.DARK);
            RenderUtils.rect(g, cx - bw / 2, by, Math.round(bw * cooldown), 2, color);
        }
    }

    private void arm(GuiGraphics g, int x, int y, int w, int h, int color) {
        if (outline.isOn()) RenderUtils.rect(g, x - 1, y - 1, w + 2, h + 2, 0xB0000000);
        RenderUtils.rect(g, x, y, w, h, color);
    }
}
