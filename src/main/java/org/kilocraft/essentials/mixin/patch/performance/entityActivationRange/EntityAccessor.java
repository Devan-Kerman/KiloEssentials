package org.kilocraft.essentials.mixin.patch.performance.entityActivationRange;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("inNetherPortal")
    boolean isInNetherPortal();

}
