package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.Projection;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

import java.util.Locale;

/**
 * Здоровье, броня и тотем в руке над головой игроков. Метки рисуются на экране по расчёту проекции
 * и показываются только для игроков в зоне прямой видимости, то есть сквозь стены они не видны.
 * Работает от первого лица.
 */
public final class NameTagsModule extends Module {
    private final SliderSetting range = add(new SliderSetting("Дальность", 40, 10, 100, 5));
    private final BooleanSetting health = add(new BooleanSetting("Здоровье", true));
    private final BooleanSetting armor = add(new BooleanSetting("Броня", true));
    private final BooleanSetting totems = add(new BooleanSetting("Тотем в руке", true));
    private final BooleanSetting distance = add(new BooleanSetting("Дистанция", true));

    public NameTagsModule() {
        super("NameTags", "Здоровье, броня и тотемы над игроками (только в прямой видимости)", Category.VISUALS);
    }

    @Override
    public void renderOverlay(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer me = mc.player;
        if (me == null || mc.level == null || !mc.options.getCameraType().isFirstPerson()) return;

        float pt = delta.getGameTimeDeltaPartialTick(false);
        Projection proj = Projection.of(me, pt, g.guiWidth(), g.guiHeight());

        for (AbstractClientPlayer e : mc.level.players()) {
            if (e == me || e.isSpectator() || e.isInvisible() || !e.isAlive()) continue;
            if (me.distanceTo(e) > range.getInt() || !me.hasLineOfSight(e)) continue;

            double px = Mth.lerp(pt, e.xo, e.getX());
            double py = Mth.lerp(pt, e.yo, e.getY()) + e.getBbHeight() + 0.35;
            double pz = Mth.lerp(pt, e.zo, e.getZ());
            int[] screen = proj.toScreen(px, py, pz);
            if (screen == null) continue;
            drawTag(g, e, me, screen[0], screen[1]);
        }
    }

    private void drawTag(GuiGraphics g, AbstractClientPlayer e, LocalPlayer me, int cx, int bottom) {
        String name = e.getName().getString();
        String dist = distance.isOn() ? " " + Math.round(me.distanceTo(e)) + "m" : "";
        float hp = e.getHealth() + e.getAbsorptionAmount();
        float maxHp = Math.max(1f, e.getMaxHealth());
        String hpText = String.format(Locale.ROOT, "%.1f", hp);

        int totemCount = 0;
        if (totems.isOn()) {
            if (e.getMainHandItem().is(Items.TOTEM_OF_UNDYING)) totemCount += e.getMainHandItem().getCount();
            if (e.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) totemCount += e.getOffhandItem().getCount();
        }
        boolean extra = armor.isOn() || totemCount > 0;

        int headerW = RenderUtils.textWidth(name + dist) + (health.isOn() ? RenderUtils.textWidth(hpText) + 8 : 0);
        int w = Math.max(72, headerW + 12);
        int h = 16 + (health.isOn() ? 8 : 0) + (extra ? 11 : 0);
        int x = cx - w / 2;
        int y = bottom - h;

        RenderUtils.panel(g, x, y, w, h, InterfaceModule.background(), InterfaceModule.accent());
        int ty = y + 4;
        RenderUtils.text(g, name, x + 5, ty, RenderUtils.WHITE);
        if (!dist.isEmpty()) RenderUtils.text(g, dist, x + 5 + RenderUtils.textWidth(name), ty, RenderUtils.GRAY);
        if (health.isOn()) RenderUtils.textRight(g, hpText, x + w - 5, ty, RenderUtils.WHITE);
        ty += 11;

        if (health.isOn()) {
            float ratio = Math.max(0f, Math.min(1f, hp / maxHp));
            int color = ratio > 0.5f ? 0xFF55FF55 : ratio > 0.25f ? 0xFFFFFF55 : 0xFFFF5555;
            RenderUtils.rect(g, x + 5, ty, w - 10, 3, RenderUtils.DARK);
            RenderUtils.rect(g, x + 5, ty, Math.round((w - 10) * ratio), 3, color);
            ty += 6;
        }

        if (extra) {
            if (armor.isOn()) RenderUtils.text(g, "Броня " + e.getArmorValue(), x + 5, ty, RenderUtils.GRAY);
            if (totemCount > 0) RenderUtils.textRight(g, "Тотем x" + totemCount, x + w - 5, ty, 0xFFFFD34D);
        }
    }
}
