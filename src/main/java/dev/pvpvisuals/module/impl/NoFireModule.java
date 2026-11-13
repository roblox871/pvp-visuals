package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.mixin.MixinStatus;
import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.Notifications;

/** Огонь на экране при горении: полностью скрыть или опустить ниже. Работает через ScreenEffectRendererMixin. */
public final class NoFireModule extends Module {
    private static NoFireModule instance;

    private final ModeSetting mode = add(new ModeSetting("Огонь", "Hidden", "Hidden", "Low"));
    private final SliderSetting offset = add(new SliderSetting("Опускание (для Low)", 0.25, 0.05, 0.5, 0.05));

    private int burningTicks;
    private boolean warned;

    public NoFireModule() {
        super("No Fire", "Скрыть или опустить огонь на экране", Category.VISUALS);
        instance = this;
    }

    public static boolean hide() {
        return instance != null && instance.isEnabled() && instance.mode.is("Hidden");
    }

    public static boolean low() {
        return instance != null && instance.isEnabled() && instance.mode.is("Low");
    }

    public static float offset() {
        return instance == null ? 0f : instance.offset.getFloat();
    }

    @Override
    public void onEnable() {
        burningTicks = 0;
        warned = false;
    }

    @Override
    public void onTick() {
        if (warned || mc.player == null || !mc.player.isOnFire()) return;
        if (++burningTicks >= 40 && !MixinStatus.fireApplied) {
            warned = true;
            Notifications.warn("No Fire: не поддерживается в этой версии");
        }
    }
}
