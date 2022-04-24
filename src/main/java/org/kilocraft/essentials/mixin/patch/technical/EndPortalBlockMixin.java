package org.kilocraft.essentials.mixin.patch.technical;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.kilocraft.essentials.config.KiloConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public abstract class EndPortalBlockMixin {

    @Inject(
            method = "entityInside",
            at = @At("HEAD"),
            cancellable = true
    )
    public void allowEnd(BlockState blockState, Level level, BlockPos blockPos, Entity entity, CallbackInfo ci) {
        if (!KiloConfig.main().world().allowEnd) ci.cancel();
    }

}
