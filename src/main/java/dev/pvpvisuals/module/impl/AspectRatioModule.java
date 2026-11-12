package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.mixin.MixinStatus;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.Notifications;

/** Растянутое разрешение (например 4:3) для картинки мира. HUD остаётся как есть. Работает через миксин GameRendererMixin. */
public final class AspectRatioModule extends Module {
    private static AspectRatioModule instance;

    private final ModeSetting preset = add(new ModeSetting("Пресет", "4:3", "4:3", "5:4", "16:10", "1:1", "21:9", "Своё"));
    private final SliderSetting custom = add(new SliderSetting("Своё значение", 1.5, 0.8, 2.6, 0.01));

    private int ticks;
    private boolean warned;

    public AspectRatioModule() {
        super("Aspect Ratio", "Растянутое разрешение мира (4:3 и другие)", Category.VISUALS);
        instance = this;
    }

    /** Желаемое соотношение сторон или -1, если модуль выключен. */
    public static float targetRatio() {
        if (instance == null || !instance.isEnabled()) return -1f;
        return instance.value();
    }

    private float value() {
        return switch (preset.get()) {
            case "4:3" -> 4f / 3f;
            case "5:4" -> 5f / 4f;
            case "16:10" -> 16f / 10f;
            case "1:1" -> 1f;
            case "21:9" -> 21f / 9f;
            default -> custom.getFloat();
        };
    }

    @Override
    public void onEnable() {
        ticks = 0;
        warned = false;
    }

    @Override
    public void onTick() {
        if (mc.level == null || warned) return;
        if (++ticks >= 100 && !MixinStatus.aspectApplied) {
            warned = true;
            Notifications.warn("Aspect Ratio: не поддерживается в этой версии");
        }
    }
}
