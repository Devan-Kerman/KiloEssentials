package org.kilocraft.essentials.mixin.patch.technical;

import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.SignedMessage;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.players.PlayerList;
import org.kilocraft.essentials.api.KiloEssentials;
import org.kilocraft.essentials.config.KiloConfig;
import org.kilocraft.essentials.util.EssentialPermission;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void disconnect(Component reason);

    @Redirect(
            method = "handleChat(Lnet/minecraft/network/protocol/game/ServerboundChatPacket;Lnet/minecraft/server/network/TextFilter$FilteredText;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;broadcastChatMessage(Lnet/minecraft/network/chat/SignedMessage;Lnet/minecraft/server/network/TextFilter$FilteredText;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceKey;)V"
            )
    )
    public void onBroadcastChatMessage(PlayerList playerList, SignedMessage signedMessage, TextFilter.FilteredText message, ServerPlayer serverPlayer, ResourceKey<ChatType> resourceKey) {
        if (!KiloConfig.main().chat().useVanillaChat) {
            KiloEssentials.getUserManager().onChatMessage(this.player, message);
        }
    }

    // Allow adventure formatting on signs
    // TODO:
    /*@Redirect(
            method = "updateSignText",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/network/chat/TextComponent;<init>(Ljava/lang/String;)V"
            )
    )
    public TextComponent useAdventureFormatting(String input) {
        if (!KiloEssentials.hasPermissionNode(this.player.createCommandSourceStack(), EssentialPermission.SIGN_COLOR)) {
            input = ComponentText.clearFormatting(input);
        }
        return (TextComponent) ComponentText.toText(input);
    }*/

}
