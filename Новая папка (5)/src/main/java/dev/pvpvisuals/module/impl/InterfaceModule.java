package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.setting.SliderSetting;

/** Общий стиль: акцентный цвет и прозрачность панелей для всех HUD-модулей и ClickGUI. */
public final class InterfaceModule extends Module {
    private static InterfaceModule instance;

    public final ColorSetting accent = add(new ColorSetting("Цвет", 0xFF8A5CFF));
    public final SliderSetting opacity = add(new SliderSetting("Прозрачность", 176, 60, 255, 1));

    public InterfaceModule() {
        super("Interface", "Общий цвет и прозрачность интерфейса", Category.VISUALS);
        this.enabled = true;
        instance = this;
    }

    public static int accent() {
        return instance == null ? 0xFF8A5CFF : instance.accent.get();
    }

    public static int background() {
        int a = instance == null ? 176 : instance.opacity.getInt();
        return (a << 24) | 0x101014;
    }
}
