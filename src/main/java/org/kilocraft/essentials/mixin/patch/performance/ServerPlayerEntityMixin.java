package org.kilocraft.essentials.mixin.patch.performance;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    // Moves operation to PlayerManager
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;moveToSpawn(Lnet/minecraft/server/world/ServerWorld;)V"))
    private void cancelOp(ServerPlayerEntity serverPlayerEntity, ServerWorld world) {
    }

}