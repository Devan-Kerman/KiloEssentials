package org.kilocraft.essentials.mixin.patch.technical;

import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.kilocraft.essentials.api.KiloEssentials;
import org.kilocraft.essentials.api.user.OnlineUser;
import org.kilocraft.essentials.user.ServerUserManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow public ServerPlayer player;

    @Inject(
            method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;filterTextPacket(Ljava/lang/String;Ljava/util/function/Consumer;)V"
            ),
            cancellable = true
    )
    public void checkMute(ServerboundChatPacket serverboundChatPacket, CallbackInfo ci) {
        OnlineUser user = KiloEssentials.getUserManager().getOnline(this.player);
        if (KiloEssentials.getUserManager().getMutedPlayerList().isMuted(this.player.getGameProfile())) {
            user.sendMessage(ServerUserManager.getMuteMessage(user));
            ci.cancel();
        }
    }

}
