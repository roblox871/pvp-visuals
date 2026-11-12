package dev.pvpvisuals;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.CloudStatus;
import net.minecraft.server.level.ParticleStatus;

/** Снижает нагрузку на графику. Исходные значения запоминаются и возвращаются при выключении. */
public final class FpsBoost {
    private FpsBoost() {}

    private static boolean saved = false;
    private static int renderDistance;
    private static int simulationDistance;
    private static double entityScaling;
    private static int biomeBlend;
    private static boolean ambientOcclusion;
    private static ParticleStatus particles;
    private static CloudStatus clouds;

    public static void apply(boolean enable) {
        Options o = Minecraft.getInstance().options;
        if (enable) {
            if (!saved) {
                renderDistance = o.renderDistance().get();
                simulationDistance = o.simulationDistance().get();
                entityScaling = o.entityDistanceScaling().get();
                biomeBlend = o.biomeBlendRadius().get();
                ambientOcclusion = o.ambientOcclusion().get();
                particles = o.particles().get();
                clouds = o.cloudStatus().get();
                saved = true;
            }
            o.renderDistance().set(Math.min(renderDistance, 8));
            o.simulationDistance().set(Math.min(simulationDistance, 6));
            o.entityDistanceScaling().set(0.75);
            o.biomeBlendRadius().set(0);
            o.ambientOcclusion().set(false);
            o.particles().set(ParticleStatus.MINIMAL);
            o.cloudStatus().set(CloudStatus.OFF);
        } else if (saved) {
            o.renderDistance().set(renderDistance);
            o.simulationDistance().set(simulationDistance);
            o.entityDistanceScaling().set(entityScaling);
            o.biomeBlendRadius().set(biomeBlend);
            o.ambientOcclusion().set(ambientOcclusion);
            o.particles().set(particles);
            o.cloudStatus().set(clouds);
            saved = false;
        }
    }
}
