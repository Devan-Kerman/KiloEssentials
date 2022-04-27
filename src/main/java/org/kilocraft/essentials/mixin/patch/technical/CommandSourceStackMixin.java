package org.kilocraft.essentials.mixin.patch.technical;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.kilocraft.essentials.util.ExtraGameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin {

    @Shadow @Final private MinecraftServer server;

    @Redirect(
            method = "broadcastToAdmins",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;sendSystemMessage(Lnet/minecraft/network/chat/Component;)V"
            )
    )
    private void shouldBroadcastToOps(ServerPlayer playerEntity, Component component) {
        if (this.server.getGameRules().getBoolean(ExtraGameRules.BROADCAST_ADMIN_COMMANDS)) playerEntity.sendSystemMessage(component);
    }

}
