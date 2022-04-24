package org.kilocraft.essentials.mixin.patch.bugfix;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin {

    // Fixes MC-250321
    @Inject(method = "getStartNode", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/pathfinder/Node;asBlockPos()Lnet/minecraft/core/BlockPos;"), cancellable = true)
    private void crashFix(BlockPos blockPos, CallbackInfoReturnable<Node> cir, Node pathNode) {
        if (pathNode == null) {
            cir.setReturnValue(null);
        }
    }
}
