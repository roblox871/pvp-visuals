package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Иконки предметов с кулдауном: красная точка - перезарядка, зелёная - готов. */
public final class CooldownsModule extends HudModule {
    private static final ItemStack[] ITEMS = {
            new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.CHORUS_FRUIT),
            new ItemStack(Items.SHIELD), new ItemStack(Items.MACE), new ItemStack(Items.WIND_CHARGE)
    };

    public CooldownsModule() {
        super("Cooldowns", "Перезарядка жемчуга, яблока, щита и булавы", -1, -1);
        width = ITEMS.length * 22 + 4;
        height = 26;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 - width / 2;
        y = sh - 112;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (mc.player == null) return;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        for (int i = 0; i < ITEMS.length; i++) {
            int ix = x + 4 + i * 22;
            RenderUtils.item(g, ITEMS[i], ix, y + 3);
            boolean cd = mc.player.getCooldowns().isOnCooldown(ITEMS[i]);
            RenderUtils.roundRect(g, ix + 5, y + 21, 6, 3, 1, cd ? 0xFFFF4D4D : 0xFF55FF55);
        }
    }
}
