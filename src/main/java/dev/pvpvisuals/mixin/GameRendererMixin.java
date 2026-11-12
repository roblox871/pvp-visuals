package dev.pvpvisuals.mixin;

import com.mojang.blaze3d.platform.Window;
import dev.pvpvisuals.module.impl.AspectRatioModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Aspect Ratio: растягивает картинку мира, подменяя соотношение сторон в матрице проекции.
 * Подменяется возвращаемое значение метода (float fovDegrees) -> Matrix4f. Если в этой версии метод называется
 * иначе, миксин не применяется (require = 0), а HUD в любом случае не затрагивается.
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getProjectionMatrix(F)Lorg/joml/Matrix4f;", at = @At("RETURN"), cancellable = true, require = 0)
    private void pvpvisuals$aspectRatio(float fovDegrees, CallbackInfoReturnable<Matrix4f> cir) {
        float target = AspectRatioModule.targetRatio();
        if (target <= 0f) return;

        Matrix4f m = cir.getReturnValue();
        Window window = Minecraft.getInstance().getWindow();
        if (m == null || window.getHeight() <= 0) return;

        float current = (float) window.getWidth() / (float) window.getHeight();
        float tan = (float) Math.tan(Math.toRadians(fovDegrees) / 2.0);
        float expected = 1f / (tan * current);

        // защита от повторного применения: масштабируем только матрицу с исходным соотношением сторон
        if (Math.abs(m.m00() - expected) > Math.abs(expected) * 0.02f) return;

        m.m00(m.m00() * current / target);
        cir.setReturnValue(m);
        MixinStatus.aspectApplied = true;
    }
}
