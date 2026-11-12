package dev.pvpvisuals.mixin;

import dev.pvpvisuals.module.impl.FogColorModule;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fog: подмешивает свой цвет к цвету тумана. Цель задана строкой, чтобы сборка не зависела от наличия класса,
 * а метод указан с полным дескриптором, чтобы не зацепить перегрузки. Если в 1.21.11 сигнатура отличается,
 * миксин не применяется (require = 0) и модуль покажет уведомление.
 * Меняется возвращаемый Vector4f (RGBA цвета тумана); дистанции начала и конца тумана здесь не меняются.
 */
@Mixin(targets = "net.minecraft.client.renderer.fog.FogRenderer")
public abstract class FogRendererMixin {
    @Inject(
            method = "setupFog(Lnet/minecraft/client/Camera;IZLnet/minecraft/client/DeltaTracker;FLnet/minecraft/client/multiplayer/ClientLevel;)Lorg/joml/Vector4f;",
            at = @At("RETURN"),
            cancellable = true,
            require = 0
    )
    private void pvpvisuals$fogColor(Camera camera, int renderDistance, boolean thick, DeltaTracker tracker,
                                     float skyDarkness, ClientLevel level, CallbackInfoReturnable<Vector4f> cir) {
        if (!FogColorModule.isActive()) return;
        Vector4f color = cir.getReturnValue();
        if (color == null) return;

        float s = FogColorModule.strength();
        color.x = color.x + (FogColorModule.red() - color.x) * s;
        color.y = color.y + (FogColorModule.green() - color.y) * s;
        color.z = color.z + (FogColorModule.blue() - color.z) * s;
        cir.setReturnValue(color);
        MixinStatus.fogApplied = true;
    }
}
