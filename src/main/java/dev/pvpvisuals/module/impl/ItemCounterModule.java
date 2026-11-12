package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Количество важных предметов в инвентаре с иконками. */
public final class ItemCounterModule extends HudModule {
    private record Entry(ItemStack icon, int count) {}

    private final BooleanSetting crystals = add(new BooleanSetting("Кристаллы", true));
    private final BooleanSetting pearls = add(new BooleanSetting("Жемчуг", true));
    private final BooleanSetting gapples = add(new BooleanSetting("Яблоки", true));
    private final BooleanSetting totems = add(new BooleanSetting("Тотемы", true));
    private final BooleanSetting xp = add(new BooleanSetting("Пузырьки опыта", false));
    private final BooleanSetting obsidian = add(new BooleanSetting("Обсидиан", false));

    private final List<Entry> entries = new ArrayList<>();
    private int tick;

    public ItemCounterModule() {
        super("Item Counter", "Количество предметов в инвентаре", 6, 230);
    }

    private static int count(LocalPlayer p, Item... items) {
        int n = 0;
        for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
            ItemStack st = p.getInventory().getItem(i);
            for (Item item : items) {
                if (st.is(item)) {
                    n += st.getCount();
                    break;
                }
            }
        }
        return n;
    }

    @Override
    public void onTick() {
        LocalPlayer p = mc.player;
        if (p == null || tick++ % 10 != 0) return;
        entries.clear();
        if (crystals.isOn()) entries.add(new Entry(new ItemStack(Items.END_CRYSTAL), count(p, Items.END_CRYSTAL)));
        if (pearls.isOn()) entries.add(new Entry(new ItemStack(Items.ENDER_PEARL), count(p, Items.ENDER_PEARL)));
        if (gapples.isOn()) {
            entries.add(new Entry(new ItemStack(Items.GOLDEN_APPLE), count(p, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE)));
        }
        if (totems.isOn()) entries.add(new Entry(new ItemStack(Items.TOTEM_OF_UNDYING), count(p, Items.TOTEM_OF_UNDYING)));
        if (xp.isOn()) entries.add(new Entry(new ItemStack(Items.EXPERIENCE_BOTTLE), count(p, Items.EXPERIENCE_BOTTLE)));
        if (obsidian.isOn()) entries.add(new Entry(new ItemStack(Items.OBSIDIAN), count(p, Items.OBSIDIAN)));
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (entries.isEmpty()) {
            width = 40;
            height = 14;
            return;
        }
        width = 52;
        height = entries.size() * 18 + 4;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        int ty = y + 3;
        for (Entry e : entries) {
            RenderUtils.item(g, e.icon(), x + 4, ty);
            int color = e.count() == 0 ? 0xFFFF7070 : RenderUtils.WHITE;
            RenderUtils.textRight(g, String.valueOf(e.count()), x + width - 5, ty + 4, color);
            ty += 18;
        }
    }
}
