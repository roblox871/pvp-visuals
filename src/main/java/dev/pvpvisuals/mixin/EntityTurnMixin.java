package dev.pvpvisuals.mixin;

import dev.pvpvisuals.module.impl.FreeLookModule;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** FreeLook: пока он включён, движение мыши крутит камеру, а не игрока. */
@Mixin(targets = "net.minecraft.world.entity.Entity")
public abstract class EntityTurnMixin {
    @Inject(method = "turn(DD)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvpvisuals$turn(double yRot, double xRot, CallbackInfo ci) {
        if (!FreeLookModule.active() || (Object) this != Minecraft.getInstance().player) return;
        FreeLookModule.turn(yRot, xRot);
        MixinStatus.turnApplied = true;
        ci.cancel();
    }
}
