package dev.pvpvisuals.mixin;

import dev.pvpvisuals.module.impl.TimeChangerModule;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Время суток только на вашем экране. Подменяется getDayTime() для клиентского мира. */
@Mixin(targets = "net.minecraft.world.level.Level")
public abstract class LevelTimeMixin {
    @Inject(method = "getDayTime()J", at = @At("RETURN"), cancellable = true, require = 0)
    private void pvpvisuals$dayTime(CallbackInfoReturnable<Long> cir) {
        if ((Object) this != Minecraft.getInstance().level) return;
        long t = TimeChangerModule.fixedTime();
        if (t < 0) return;
        cir.setReturnValue(t);
        MixinStatus.timeApplied = true;
    }
}
