package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.ColorSetting;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;

/** Общий стиль интерфейса: два цвета для градиента, стиль панелей, скругление, тень, уведомления. */
public final class InterfaceModule extends Module {
    private static InterfaceModule instance;

    public final ColorSetting accent = add(new ColorSetting("Цвет 1", 0xFF8A5CFF));
    public final ColorSetting accent2 = add(new ColorSetting("Цвет 2", 0xFF4DA3FF));
    public final ModeSetting style = add(new ModeSetting("Стиль", "Glass", "Glass", "Solid", "Minimal"));
    public final SliderSetting opacity = add(new SliderSetting("Прозрачность", 170, 60, 255, 1));
    public final SliderSetting radius = add(new SliderSetting("Скругление", 3, 0, 6, 1));
    public final BooleanSetting shadow = add(new BooleanSetting("Тень панелей", true));
    public final BooleanSetting textShadow = add(new BooleanSetting("Тень текста", false));
    public final BooleanSetting notifications = add(new BooleanSetting("Уведомления", true));

    public InterfaceModule() {
        super("Interface", "Цвета, стиль и уведомления интерфейса", Category.VISUALS);
        this.enabled = true;
        instance = this;
    }

    public static int accent() {
        return instance == null ? 0xFF8A5CFF : instance.accent.get();
    }

    public static int accent2() {
        return instance == null ? 0xFF4DA3FF : instance.accent2.get();
    }

    public static int background() {
        if (instance == null) return 0xAA101014;
        int a = instance.opacity.getInt();
        if (instance.style.is("Solid")) a = Math.max(a, 225);
        return (a << 24) | 0x101014;
    }

    public static String styleName() {
        return instance == null ? "Glass" : instance.style.get();
    }

    public static int radius() {
        return instance == null ? 3 : instance.radius.getInt();
    }

    public static boolean shadow() {
        return instance == null || instance.shadow.isOn();
    }

    public static boolean textShadow() {
        return instance != null && instance.textShadow.isOn();
    }

    public static boolean notificationsOn() {
        return instance == null || instance.notifications.isOn();
    }
}
