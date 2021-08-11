package org.kilocraft.essentials.mixin.patch;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.kilocraft.essentials.patch.ChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobNavigation.class)
public abstract class MobNavigationMixin {

    /*
    * Copied from:
    * https://github.com/Wesley1808/ServerCore-Fabric/blob/1.17.1/src/main/java/org/provim/servercore/mixin/performance/MobNavigationMixin.java
    * */

    // Don't load chunks for pathfinding.
    @Redirect(method = "findPathTo(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
    private BlockState ifChunkLoaded(World world, BlockPos pos) {
        return ChunkManager.getStateIfVisible(world, pos);
    }
}
