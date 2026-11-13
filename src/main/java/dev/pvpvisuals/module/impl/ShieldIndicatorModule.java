package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Индикатор щита: зелёный, когда щит готов или поднят, красный, когда он на кулдауне (например, после удара топором). */
public final class ShieldIndicatorModule extends HudModule {
    private static final ItemStack ICON = new ItemStack(Items.SHIELD);

    public ShieldIndicatorModule() {
        super("Shield Indicator", "Состояние щита: зелёный готов, красный кулдаун", -1, -1);
        width = 84;
        height = 24;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 + 14;
        y = sh / 2 + 60;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null) return;

        ItemStack shield = p.getMainHandItem().is(Items.SHIELD) ? p.getMainHandItem()
                : p.getOffhandItem().is(Items.SHIELD) ? p.getOffhandItem() : ItemStack.EMPTY;
        if (shield.isEmpty()) return;

        boolean cooldown = p.getCooldowns().isOnCooldown(shield);
        int color = cooldown ? 0xFFFF4D4D : 0xFF55FF55;
        String text = cooldown ? "Кулдаун" : p.isBlocking() ? "Блок" : "Готов";

        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), color);
        RenderUtils.item(g, ICON, x + 4, y + 4);
        RenderUtils.roundRect(g, x + 24, y + 9, 6, 6, 3, color);
        RenderUtils.text(g, text, x + 34, y + 8, color);
    }
}
