package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Предупреждения: низкое здоровье, нет тотема в левой руке, броня почти сломана. */
public final class AlertsModule extends HudModule {
    private static final EquipmentSlot[] ARMOR = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private final SliderSetting lowHp = add(new SliderSetting("Здоровье ниже", 6, 2, 12, 1));
    private final BooleanSetting health = add(new BooleanSetting("Низкое здоровье", true));
    private final BooleanSetting totem = add(new BooleanSetting("Нет тотема в левой руке", true));
    private final BooleanSetting armor = add(new BooleanSetting("Броня почти сломана", true));

    private final List<String> alerts = new ArrayList<>();
    private int tick;

    public AlertsModule() {
        super("Alerts", "Предупреждения о здоровье, тотеме и броне", -1, -1);
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 - 60;
        y = sh / 2 - 60;
    }

    @Override
    public void onTick() {
        LocalPlayer p = mc.player;
        if (p == null || tick++ % 5 != 0) return;
        alerts.clear();
        if (health.isOn() && p.getHealth() <= lowHp.getInt()) alerts.add("Низкое здоровье");
        if (totem.isOn() && !p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) alerts.add("Нет тотема в левой руке");
        if (armor.isOn()) {
            for (EquipmentSlot slot : ARMOR) {
                ItemStack st = p.getItemBySlot(slot);
                if (!st.isEmpty() && st.isDamageableItem()
                        && (st.getMaxDamage() - st.getDamageValue()) * 100 / st.getMaxDamage() < 15) {
                    alerts.add("Броня почти сломана");
                    break;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (alerts.isEmpty()) {
            width = 120;
            height = 14;
            return;
        }
        int w = 0;
        for (String a : alerts) w = Math.max(w, RenderUtils.textWidth(a));
        width = w + 16;
        height = alerts.size() * 11 + 6;
        double pulse = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 220.0);
        int alpha = 150 + (int) (105 * pulse);
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), 0xFFFF4D4D);
        int ty = y + 4;
        for (String a : alerts) {
            RenderUtils.textCentered(g, a, x + width / 2, ty, RenderUtils.withAlpha(0xFF6B6B, alpha));
            ty += 11;
        }
    }
}
