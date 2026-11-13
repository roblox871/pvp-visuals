package dev.pvpvisuals.mixin;

import dev.pvpvisuals.module.impl.FreeLookModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * FreeLook: подменяем углы в Camera.setRotation(float yRot, float xRot) до расчёта позиции камеры, поэтому
 * камера в третьем лице правильно обходит вокруг игрока. Если сигнатура в 1.21.11 другая, миксин не применится.
 */
@Mixin(targets = "net.minecraft.client.Camera")
public abstract class CameraMixin {
    @ModifyVariable(method = "setRotation(FF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private float pvpvisuals$yaw(float yRot) {
        if (!FreeLookModule.active()) return yRot;
        MixinStatus.cameraApplied = true;
        return FreeLookModule.yaw();
    }

    @ModifyVariable(method = "setRotation(FF)V", at = @At("HEAD"), argsOnly = true, ordinal = 1, require = 0)
    private float pvpvisuals$pitch(float xRot) {
        return FreeLookModule.active() ? FreeLookModule.pitch() : xRot;
    }
}
