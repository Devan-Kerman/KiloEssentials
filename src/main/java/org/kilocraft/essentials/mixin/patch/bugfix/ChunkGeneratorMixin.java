package org.kilocraft.essentials.mixin.patch.bugfix;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.Set;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    // Fixes MC-249136
    @Inject(method = "getStructureGeneratingAt", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/StructureManager;checkStructurePresence(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/Structure;Z)Lnet/minecraft/world/level/levelgen/structure/StructureCheckResult;"), cancellable = true)
    private static void skipBuriedTreasure(Set<Holder<Structure>> set, LevelReader worldView, StructureManager structureAccessor, boolean bl, StructurePlacement structurePlacement, ChunkPos chunkPos, CallbackInfoReturnable<Pair<BlockPos, Holder<Structure>>> cir, Iterator var6, Holder<Structure> registryEntry) {
        if (registryEntry.is(StructureTags.ON_TREASURE_MAPS)) {
            cir.setReturnValue(null);
        }
    }
}