package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class ArmorHudModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private final ModeSetting layout = add(new ModeSetting("Расположение", "Vertical", "Vertical", "Horizontal"));
    private final BooleanSetting percent = add(new BooleanSetting("Проценты", true));

    public ArmorHudModule() {
        super("ArmorHUD", "Броня и её прочность", -1, -1);
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw - 60;
        y = sh / 2 - 40;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        boolean vertical = layout.is("Vertical");

        if (vertical) {
            width = percent.isOn() ? 54 : 26;
            height = 4 * 18 + 6;
        } else {
            width = 4 * 22 + 4;
            height = percent.isOn() ? 32 : 24;
        }
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());

        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack st = p.getItemBySlot(SLOTS[i]);
            if (st.isEmpty()) continue;
            int ix = vertical ? x + 4 : x + 4 + i * 22;
            int iy = vertical ? y + 4 + i * 18 : y + 4;
            RenderUtils.item(g, st, ix, iy);
            if (!percent.isOn() || !st.isDamageableItem()) continue;

            int pct = (st.getMaxDamage() - st.getDamageValue()) * 100 / st.getMaxDamage();
            int color = pct > 50 ? 0xFF55FF55 : pct > 20 ? 0xFFFFFF55 : 0xFFFF5555;
            if (vertical) {
                RenderUtils.text(g, pct + "%", ix + 22, iy + 4, color);
            } else {
                RenderUtils.textCentered(g, pct + "%", ix + 8, iy + 18, color);
            }
        }
    }
}
