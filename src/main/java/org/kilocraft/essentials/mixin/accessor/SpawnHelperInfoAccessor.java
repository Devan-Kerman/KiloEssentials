package org.kilocraft.essentials.mixin.accessor;

import net.minecraft.world.SpawnDensityCapper;
import net.minecraft.world.SpawnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpawnHelper.Info.class)
public interface SpawnHelperInfoAccessor {

    @Accessor("densityCapper")
    SpawnDensityCapper getDensityCapper();

}
