package org.kilocraft.essentials.mixin.patch.gameplay;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.kilocraft.essentials.util.settings.ServerSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l, i);
    }

    // Change global to local sound events
    @Inject(
            method = "globalLevelEvent",
            at = @At("HEAD"),
            cancellable = true
    )
    public void shouldWeAnnoyEveryone(int i, BlockPos blockPos, int j, CallbackInfo ci) {
        if (!ServerSettings.getBoolean("patch.global_sound")) {
            ci.cancel();
            SoundEvent soundEvent = null;
            float g = 1.0F;
            float h = 1.0F;
            switch (i) {
                case 1023 -> soundEvent = SoundEvents.WITHER_SPAWN;
                case 1028 -> {
                    soundEvent = SoundEvents.ENDER_DRAGON_DEATH;
                    g = 5.0F;
                }
                case 1038 -> soundEvent = SoundEvents.END_PORTAL_SPAWN;
            }
            if (soundEvent != null)
                this.playSound(null, blockPos, soundEvent, SoundSource.HOSTILE, g, h);
        }
    }

}
