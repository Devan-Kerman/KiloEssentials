package org.kilocraft.essentials.mixin.patch;

import net.minecraft.class_5778;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_5778.class)
public class class_5778Mixin {
    @Redirect(method = "method_33362", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isStill()Z"))
    public boolean canReplace(FluidState fluidState) {
        return fluidState.isStill() && fluidState.getFluid() instanceof WaterFluid;
    }
}
