package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.BooleanSetting;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.HitTracker;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.ThreadLocalRandom;

/** Косметика ударов: частицы на цели при попадании и эффект при убийстве. Видны только вам. */
public final class HitParticlesModule extends Module {
    private final ModeSetting type = add(new ModeSetting("Частицы", "Crit", "Crit", "Enchant", "Heart", "Flame", "Soul", "Firework"));
    private final SliderSetting count = add(new SliderSetting("Количество", 8, 1, 30, 1));
    private final BooleanSetting killEffect = add(new BooleanSetting("Эффект при убийстве", true));

    private int seenHits;
    private LivingEntity watched;
    private long watchedUntil;

    public HitParticlesModule() {
        super("Hit Particles", "Частицы при ударе и убийстве", Category.VISUALS);
    }

    private ParticleOptions particle() {
        return switch (type.get()) {
            case "Enchant" -> ParticleTypes.ENCHANTED_HIT;
            case "Heart" -> ParticleTypes.HEART;
            case "Flame" -> ParticleTypes.FLAME;
            case "Soul" -> ParticleTypes.SOUL_FIRE_FLAME;
            case "Firework" -> ParticleTypes.FIREWORK;
            default -> ParticleTypes.CRIT;
        };
    }

    private void burst(LivingEntity e, ParticleOptions p, int n) {
        if (mc.level == null) return;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double cx = e.getX(), cy = e.getY() + e.getBbHeight() * 0.6, cz = e.getZ();
        for (int i = 0; i < n; i++) {
            mc.level.addParticle(p,
                    cx + (r.nextDouble() - 0.5) * 0.6, cy + (r.nextDouble() - 0.5) * 0.6, cz + (r.nextDouble() - 0.5) * 0.6,
                    (r.nextDouble() - 0.5) * 0.3, r.nextDouble() * 0.25, (r.nextDouble() - 0.5) * 0.3);
        }
    }

    @Override
    public void onTick() {
        if (mc.level == null) return;
        int h = HitTracker.hits();
        if (h != seenHits) {
            seenHits = h;
            LivingEntity e = HitTracker.lastEntity();
            if (e != null) {
                burst(e, particle(), count.getInt());
                watched = e;
                watchedUntil = System.currentTimeMillis() + 2500;
            }
        }
        if (killEffect.isOn() && watched != null) {
            if (!watched.isAlive()) {
                burst(watched, ParticleTypes.TOTEM_OF_UNDYING, 30);
                watched = null;
            } else if (System.currentTimeMillis() > watchedUntil) {
                watched = null;
            }
        }
    }
}
