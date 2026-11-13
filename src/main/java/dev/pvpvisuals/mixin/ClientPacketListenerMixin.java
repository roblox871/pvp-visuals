package dev.pvpvisuals.mixin;

import dev.pvpvisuals.module.impl.TotemPopsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Событие 35 - сработал тотем бессмертия. Считаем попы у игроков. */
@Mixin(targets = "net.minecraft.client.multiplayer.ClientPacketListener")
public abstract class ClientPacketListenerMixin {
    @Inject(
            method = "handleEntityEvent(Lnet/minecraft/network/protocol/game/ClientboundEntityEventPacket;)V",
            at = @At("HEAD"),
            require = 0
    )
    private void pvpvisuals$entityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
        if (packet.getEventId() != 35 || !TotemPopsModule.enabledNow()) return;
        Entity e = packet.getEntity(Minecraft.getInstance().level);
        if (e == null) return;
        TotemPopsModule.pop(e);
        MixinStatus.popApplied = true;
    }
}
