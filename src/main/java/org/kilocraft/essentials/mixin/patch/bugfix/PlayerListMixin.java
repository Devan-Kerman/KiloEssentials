package org.kilocraft.essentials.mixin.patch.bugfix;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ChestBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Inject(method = "remove", at = @At("HEAD"))
    private void keepChestBoats(ServerPlayer player, CallbackInfo ci) {
        if (player.isPassenger()) {
            Entity rootVehicle = player.getRootVehicle();
            if (rootVehicle instanceof ChestBoat) {
                player.stopRiding();
                return;
            }

            for (Entity vehicle : rootVehicle.getPassengers()) {
                if (vehicle instanceof ChestBoat) {
                    player.stopRiding();
                    return;
                }
            }
        }
    }
}