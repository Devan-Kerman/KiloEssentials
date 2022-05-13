package org.kilocraft.essentials.mixin.patch.technical;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.kilocraft.essentials.api.KiloEssentials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow
    protected abstract MutableComponent decorateDisplayNameComponent(MutableComponent mutableComponent);

    @Inject(
            method = "getDisplayName",
            at = @At("HEAD"),
            cancellable = true
    )
    public void addLuckPermsPrefix(CallbackInfoReturnable<Component> cir) {
        if (KiloEssentials.getInstance().hasLuckPerms()) {
            MutableComponent text = (MutableComponent) KiloEssentials.getUserManager().getOnline((ServerPlayer) (Object) this).getRankedDisplayName();
            cir.setReturnValue(this.decorateDisplayNameComponent(text));
        }
    }

}
