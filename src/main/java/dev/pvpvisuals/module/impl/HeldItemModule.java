package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

/** Предмет в руке: иконка, название и прочность. */
public final class HeldItemModule extends HudModule {
    public HeldItemModule() {
        super("Held Item", "Предмет в руке и его прочность", -1, -1);
        width = 110;
        height = 24;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = sw / 2 - width / 2;
        y = sh - 82;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        LocalPlayer p = mc.player;
        if (p == null) return;
        ItemStack st = p.getMainHandItem();
        if (st.isEmpty()) {
            width = 60;
            height = 14;
            RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
            RenderUtils.text(g, "Пусто", x + 5, y + 3, RenderUtils.GRAY);
            return;
        }
        String name = st.getHoverName().getString();
        width = Math.max(90, RenderUtils.textWidth(name) + 34);
        height = 24;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        RenderUtils.item(g, st, x + 4, y + 4);
        RenderUtils.text(g, name, x + 24, y + 4, RenderUtils.WHITE);
        if (st.isDamageableItem()) {
            int left = st.getMaxDamage() - st.getDamageValue();
            float ratio = Math.max(0f, Math.min(1f, left / (float) st.getMaxDamage()));
            int color = ratio > 0.5f ? 0xFF55FF55 : ratio > 0.2f ? 0xFFFFFF55 : 0xFFFF5555;
            RenderUtils.rect(g, x + 24, y + 16, width - 30, 3, RenderUtils.DARK);
            RenderUtils.rect(g, x + 24, y + 16, Math.round((width - 30) * ratio), 3, color);
        } else if (st.getCount() > 1) {
            RenderUtils.textRight(g, "x" + st.getCount(), x + width - 5, y + 14, RenderUtils.GRAY);
        }
    }
}
