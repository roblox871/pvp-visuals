package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.HudModule;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.util.CpsCounter;
import dev.pvpvisuals.util.RenderUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public final class KeystrokesModule extends HudModule {
    private final BooleanSetting space = add(new BooleanSetting("Пробел", true));
    private final BooleanSetting mouse = add(new BooleanSetting("Мышь и CPS", true));

    public KeystrokesModule() {
        super("Keystrokes", "Нажатые клавиши и кнопки мыши", -1, -1);
        width = 58;
        height = 58;
    }

    @Override
    protected void initPosition(int sw, int sh) {
        x = 6;
        y = sh - 66;
    }

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        int accent = InterfaceModule.accent();
        width = 58;
        height = 38;

        key(g, x + 20, y, 18, 18, "W", mc.options.keyUp.isDown(), accent);
        key(g, x, y + 20, 18, 18, "A", mc.options.keyLeft.isDown(), accent);
        key(g, x + 20, y + 20, 18, 18, "S", mc.options.keyDown.isDown(), accent);
        key(g, x + 40, y + 20, 18, 18, "D", mc.options.keyRight.isDown(), accent);

        int cy = y + 40;
        if (space.isOn()) {
            key(g, x, cy, 58, 12, "", mc.options.keyJump.isDown(), accent);
            RenderUtils.rect(g, x + 18, cy + 5, 22, 1, mc.options.keyJump.isDown() ? RenderUtils.WHITE : RenderUtils.GRAY);
            cy += 14;
            height = cy - y - 2;
        }
        if (mouse.isOn()) {
            key(g, x, cy, 29, 18, "L " + CpsCounter.left(), mc.mouseHandler.isLeftPressed(), accent);
            key(g, x + 29, cy, 29, 18, "R " + CpsCounter.right(), mc.mouseHandler.isRightPressed(), accent);
            height = cy + 18 - y;
        }
    }

    private static void key(GuiGraphics g, int x, int y, int w, int h, String label, boolean down, int accent) {
        int bg = down ? RenderUtils.withAlpha(accent, 200) : InterfaceModule.background();
        RenderUtils.roundRect(g, x, y, w, h, 2, bg);
        if (!label.isEmpty()) {
            RenderUtils.textCentered(g, label, x + w / 2, y + (h - 8) / 2, down ? RenderUtils.WHITE : RenderUtils.GRAY);
        }
    }
}
