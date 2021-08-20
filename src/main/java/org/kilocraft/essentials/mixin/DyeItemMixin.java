package org.kilocraft.essentials.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import org.kilocraft.essentials.mixin.accessor.ShulkerEntityAccessor;
import org.kilocraft.essentials.util.settings.ServerSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyeItem.class)
public class DyeItemMixin {

    @Shadow @Final private DyeColor color;

    @Inject(method = "useOnEntity", at = @At(value = "HEAD"), cancellable = true)
    public void dyeShulkerEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entity instanceof ShulkerEntity shulkerEntity && ServerSettings.getBoolean("patch.dye_shulkers")) {
            if (shulkerEntity.isAlive() && shulkerEntity.getColor() != this.color) {
                if (!user.world.isClient) {
                    ((ShulkerEntityAccessor)shulkerEntity).setColor(this.color);
                    user.swingHand(Hand.MAIN_HAND, true);
                    stack.decrement(1);
                }
                cir.setReturnValue(ActionResult.success(user.world.isClient));
            }
        }
    }

}