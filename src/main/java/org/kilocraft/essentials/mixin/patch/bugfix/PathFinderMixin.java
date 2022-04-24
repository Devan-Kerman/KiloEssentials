package org.kilocraft.essentials.mixin.patch.bugfix;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.Set;

@Mixin(PathFinder.class)
public abstract class PathFinderMixin {

    // Fixes MC-250321
    @Inject(method = "findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Ljava/util/Set;stream()Ljava/util/stream/Stream;"), cancellable = true)
    private void crashFix(PathNavigationRegion world, Mob mob, Set<BlockPos> positions, float followRange, int distance, float rangeMultiplier, CallbackInfoReturnable<Path> cir, Node pathNode) {
        if (pathNode == null) {
            cir.setReturnValue(null);
        }
    }
}
