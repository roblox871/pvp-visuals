package dev.pvpvisuals.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pvpvisuals.module.impl.NoFireModule;
import dev.pvpvisuals.module.impl.ViewModelModule;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Огонь на экране и анимация тотема. Сигнатуры взяты из Mojang mappings 1.21.9 (в 1.21.11 они должны совпадать):
 * renderFire(PoseStack, MultiBufferSource, TextureAtlasSprite) - private static,
 * renderItemActivationAnimation(PoseStack, float, SubmitNodeCollector) - private,
 * displayItemActivation(ItemStack, RandomSource) - public.
 * Типы, которых я не гарантирую (SubmitNodeCollector, TextureAtlasSprite), принимаются как Object через @Coerce.
 */
@Mixin(targets = "net.minecraft.client.renderer.ScreenEffectRenderer")
public abstract class ScreenEffectRendererMixin {
    /** Полностью скрыть огонь. Совпадение по имени, чтобы не зависеть от параметров. */
    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true, require = 0)
    private static void pvpvisuals$hideFire(CallbackInfo ci) {
        if (NoFireModule.hide()) {
            ci.cancel();
            MixinStatus.fireApplied = true;
        }
    }

    /** Опустить огонь: сдвигаем систему координат вниз перед отрисовкой. */
    @Inject(
            method = "renderFire(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V",
            at = @At("HEAD"),
            require = 0
    )
    private static void pvpvisuals$lowFire(PoseStack poseStack, MultiBufferSource buffer, @Coerce Object sprite, CallbackInfo ci) {
        if (NoFireModule.low()) {
            poseStack.translate(0.0f, -NoFireModule.offset(), 0.0f);
            MixinStatus.fireApplied = true;
        }
    }

    /** Скрыть анимацию тотема. */
    @Inject(method = "displayItemActivation", at = @At("HEAD"), cancellable = true, require = 0)
    private void pvpvisuals$hideTotemPop(CallbackInfo ci) {
        if (ViewModelModule.totemPopHidden()) ci.cancel();
    }

    /** Уменьшить анимацию тотема: масштаб применяется внутри push/pop. */
    @Inject(
            method = "renderItemActivationAnimation(Lcom/mojang/blaze3d/vertex/PoseStack;FLnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            at = @At("HEAD"),
            require = 0
    )
    private void pvpvisuals$popScaleIn(PoseStack poseStack, float partialTick, @Coerce Object collector, CallbackInfo ci) {
        float s = ViewModelModule.totemPopScale();
        poseStack.pushPose();
        ViewModelModule.popPushed = true;
        if (s < 0.999f) poseStack.scale(s, s, s);
    }

    @Inject(
            method = "renderItemActivationAnimation(Lcom/mojang/blaze3d/vertex/PoseStack;FLnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            at = @At("RETURN"),
            require = 0
    )
    private void pvpvisuals$popScaleOut(PoseStack poseStack, float partialTick, @Coerce Object collector, CallbackInfo ci) {
        if (ViewModelModule.popPushed) {
            ViewModelModule.popPushed = false;
            poseStack.popPose();
        }
    }
}
