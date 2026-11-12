package dev.pvpvisuals.module.impl;

import dev.pvpvisuals.module.Category;
import dev.pvpvisuals.module.Module;
import dev.pvpvisuals.setting.ModeSetting;
import dev.pvpvisuals.setting.SliderSetting;
import dev.pvpvisuals.util.HitTracker;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Свои звуки попадания: отдельные пресеты, тон и громкость для обычного удара и критического.
 * Звук добавляется к ванильному (заглушить ванильный без миксина нельзя). Слышен только вам.
 */
public final class HitSoundModule extends Module {
    private static final String[] PRESETS = {"Orb", "LevelUp", "Crit", "Strong", "Arrow", "Off"};

    private final ModeSetting normalSound = add(new ModeSetting("Звук удара", "Orb", PRESETS));
    private final SliderSetting normalPitch = add(new SliderSetting("Тон удара", 1.2, 0.5, 2.0, 0.1));
    private final SliderSetting normalVolume = add(new SliderSetting("Громкость удара", 0.6, 0.0, 1.0, 0.05));

    private final ModeSetting critSound = add(new ModeSetting("Звук крита", "LevelUp", PRESETS));
    private final SliderSetting critPitch = add(new SliderSetting("Тон крита", 1.4, 0.5, 2.0, 0.1));
    private final SliderSetting critVolume = add(new SliderSetting("Громкость крита", 0.7, 0.0, 1.0, 0.05));

    private int seenHits;

    public HitSoundModule() {
        super("Hit Sound", "Свои звуки удара и крита", Category.PLAYER);
    }

    private static SoundEvent preset(String name) {
        return switch (name) {
            case "Orb" -> SoundEvents.EXPERIENCE_ORB_PICKUP;
            case "LevelUp" -> SoundEvents.PLAYER_LEVELUP;
            case "Crit" -> SoundEvents.PLAYER_ATTACK_CRIT;
            case "Strong" -> SoundEvents.PLAYER_ATTACK_STRONG;
            case "Arrow" -> SoundEvents.ARROW_HIT_PLAYER;
            default -> null;
        };
    }

    @Override
    public void onTick() {
        int h = HitTracker.hits();
        if (h == seenHits) return;
        seenHits = h;

        LocalPlayer p = mc.player;
        if (p == null) return;
        // приблизительное определение крита: удар в падении без спринта
        boolean crit = p.fallDistance > 0.05 && !p.isSprinting();

        SoundEvent event = preset(crit ? critSound.get() : normalSound.get());
        if (event == null) return;
        float pitch = crit ? critPitch.getFloat() : normalPitch.getFloat();
        float volume = crit ? critVolume.getFloat() : normalVolume.getFloat();
        mc.getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
    }
}
