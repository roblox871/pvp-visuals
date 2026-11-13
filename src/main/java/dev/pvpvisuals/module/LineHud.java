package dev.pvpvisuals.module;

import dev.pvpvisuals.module.impl.InterfaceModule;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/** Простая плашка «подпись — значение». Размер подгоняется под текст. */
public class LineHud extends HudModule {
    private final String label;
    private final Supplier<String> value;
    private final IntSupplier color;
    private final int slot;

    public LineHud(String name, String description, int slot, String label, Supplier<String> value, IntSupplier color) {
        super(name, description, -1, -1);
        this.slot = slot;
        this.label = label;
        this.value = value;
        this.color = color;
        this.width = 80;
        this.height = 16;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = 6;
        y = 40 + slot * 20;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        if (mc.player == null) return;
        String v;
        try {
            v = value.get();
        } catch (RuntimeException e) {
            v = "-";
        }
        if (v == null) return;
        width = RenderUtils.textWidth(label) + RenderUtils.textWidth(v) + 20;
        RenderUtils.panel(g, x, y, width, height, InterfaceModule.background(), InterfaceModule.accent());
        RenderUtils.text(g, label, x + 5, y + 4, RenderUtils.GRAY);
        RenderUtils.textRight(g, v, x + width - 5, y + 4, color == null ? RenderUtils.WHITE : color.getAsInt());
    }
}
