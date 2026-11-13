package dev.pvpvisuals.mixin;

import dev.pvpvisuals.module.impl.SkyColorModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Цвет неба. Если в 1.21.11 метод getSkyColor(Vec3, float) убран (небо перенесено в новые системы), миксин не применится. */
@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel")
public abstract class ClientLevelSkyMixin {
    @Inject(method = "getSkyColor(Lnet/minecraft/world/phys/Vec3;F)I", at = @At("RETURN"), cancellable = true, require = 0)
    private void pvpvisuals$sky(CallbackInfoReturnable<Integer> cir) {
        int c = SkyColorModule.color();
        if (c == 0) return;
        cir.setReturnValue(c);
        MixinStatus.skyApplied = true;
    }
}
