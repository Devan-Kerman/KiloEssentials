package org.kilocraft.essentials.mixin.patch.bugfix;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    @Shadow
    public ServerGamePacketListenerImpl connection;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    @Inject(
            method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V",
            at = @At("RETURN")
    )
    private void updateExperience(ServerLevel serverWorld, double d, double e, double f, float g, float h, CallbackInfo ci) {
        // Update experience upon teleport
        this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
    }

}
