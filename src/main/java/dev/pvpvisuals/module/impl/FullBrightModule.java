package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Полная яркость: накладывает на вашего персонажа локальный эффект ночного зрения.
 * Сервер об этом не знает, эффект виден только вам и скрыт из списка эффектов.
 */
public final class FullBrightModule extends Module {
    private static final int LONG = 1_000_000;

    public FullBrightModule() {
        super("FullBright", "Максимальная яркость в темноте", Category.VISUALS);
    }

    /** true, если эффект создан этим модулем (его не нужно показывать в списке эффектов). */
    public static boolean owns(MobEffectInstance e) {
        return e.getEffect().equals(MobEffects.NIGHT_VISION) && e.getDuration() > LONG / 2;
    }

    @Override
    public void onTick() {
        LocalPlayer p = mc.player;
        if (p == null) return;
        if (!p.hasEffect(MobEffects.NIGHT_VISION)) {
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, LONG, 0, false, false, false));
        }
    }

    @Override
    public void onDisable() {
        LocalPlayer p = mc.player;
        if (p == null) return;
        MobEffectInstance e = p.getEffect(MobEffects.NIGHT_VISION);
        if (e != null && owns(e)) p.removeEffect(MobEffects.NIGHT_VISION);
    }
}
