package dev.pvpvisuals.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pvpvisuals.module.impl.ViewModelModule;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Положение, поворот и размер предметов в руках. Сигнатура из Mojang mappings 1.21.9:
 * renderArmWithItem(AbstractClientPlayer, float, float, InteractionHand, float, ItemStack, float, PoseStack,
 * SubmitNodeCollector, int). Если в 1.21.11 она другая, миксин не применяется и модуль покажет уведомление.
 */
@Mixin(targets = "net.minecraft.client.renderer.ItemInHandRenderer")
public abstract class ItemInHandRendererMixin {
    private static final String DESC =
            "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V";

    @Inject(method = DESC, at = @At("HEAD"), require = 0)
    private void pvpvisuals$transformIn(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
                                        float swing, ItemStack stack, float equip, PoseStack poseStack,
                                        @Coerce Object collector, int light, CallbackInfo ci) {
        if (!ViewModelModule.affects(stack)) return;
        boolean right = (hand == InteractionHand.MAIN_HAND) == (player.getMainArm() == HumanoidArm.RIGHT);
        poseStack.pushPose();
        ViewModelModule.handPushed = true;
        ViewModelModule.apply(poseStack, stack, right ? 1.0f : -1.0f);
        MixinStatus.itemApplied = true;
    }

    @Inject(method = DESC, at = @At("RETURN"), require = 0)
    private void pvpvisuals$transformOut(AbstractClientPlayer player, float partialTick, float pitch, InteractionHand hand,
                                         float swing, ItemStack stack, float equip, PoseStack poseStack,
                                         @Coerce Object collector, int light, CallbackInfo ci) {
        if (ViewModelModule.handPushed) {
            ViewModelModule.handPushed = false;
            poseStack.popPose();
        }
    }
}
