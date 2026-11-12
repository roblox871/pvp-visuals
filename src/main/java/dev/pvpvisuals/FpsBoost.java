package dev.pvpvisuals;

import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.level.ParticleStatus;

/**
 * Снижает нагрузку на графику. Исходные значения запоминаются и возвращаются при выключении.
 * Balanced: умеренное снижение дальности, без частиц и облаков.
 * Max: ещё ниже дальность, без теней сущностей и мипмапов, вертикальная синхронизация выключена, лимит кадров снят.
 */
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
    private static boolean entityShadows;
    private static boolean vsync;
    private static int mipmaps;
    private static int fpsLimit;

    public static void apply(boolean enable, boolean max) {
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
                entityShadows = o.entityShadows().get();
                vsync = o.enableVsync().get();
                mipmaps = o.mipmapLevels().get();
                fpsLimit = o.framerateLimit().get();
                saved = true;
            }
            o.renderDistance().set(Math.min(renderDistance, max ? 6 : 8));
            o.simulationDistance().set(Math.min(simulationDistance, max ? 5 : 6));
            o.entityDistanceScaling().set(max ? 0.5 : 0.75);
            o.biomeBlendRadius().set(0);
            o.ambientOcclusion().set(false);
            o.particles().set(ParticleStatus.MINIMAL);
            o.cloudStatus().set(CloudStatus.OFF);
            o.entityShadows().set(!max && entityShadows);
            if (max) {
                o.enableVsync().set(false);
                o.mipmapLevels().set(0);
                o.framerateLimit().set(260);
            } else {
                o.enableVsync().set(vsync);
                o.mipmapLevels().set(mipmaps);
                o.framerateLimit().set(fpsLimit);
            }
        } else if (saved) {
            o.renderDistance().set(renderDistance);
            o.simulationDistance().set(simulationDistance);
            o.entityDistanceScaling().set(entityScaling);
            o.biomeBlendRadius().set(biomeBlend);
            o.ambientOcclusion().set(ambientOcclusion);
            o.particles().set(particles);
            o.cloudStatus().set(clouds);
            o.entityShadows().set(entityShadows);
            o.enableVsync().set(vsync);
            o.mipmapLevels().set(mipmaps);
            o.framerateLimit().set(fpsLimit);
            saved = false;
        }
    }
}
