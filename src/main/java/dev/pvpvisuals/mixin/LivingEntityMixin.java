package dev.pvpvisuals.mixin;

import dev.pvpvisuals.module.impl.ViewModelModule;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Скорость взмаха руки (только внешний вид, на удары и сервер не влияет). Только для вашего игрока. */
@Mixin(targets = "net.minecraft.world.entity.LivingEntity")
public abstract class LivingEntityMixin {
    @Inject(method = "getCurrentSwingDuration()I", at = @At("RETURN"), cancellable = true, require = 0)
    private void pvpvisuals$swingDuration(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this != Minecraft.getInstance().player) return;
        float k = ViewModelModule.swingFactor();
        if (k == 1.0f) return;
        cir.setReturnValue(Math.max(1, Math.round(cir.getReturnValue() * k)));
        MixinStatus.swingApplied = true;
    }
}
