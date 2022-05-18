package org.kilocraft.essentials.mixin.patch.technical;

import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.server.MinecraftServer;
import org.kilocraft.essentials.api.Brandable;
import org.kilocraft.essentials.chat.KiloChatDecorator;
import org.kilocraft.essentials.provided.BrandedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements Brandable {

    @Override
    public String getServerModName() {
        return BrandedServer.getFinalBrandName();
    }

    private final ChatDecorator kiloChatDecorator = new KiloChatDecorator();

    @Inject(
            method = "getChatDecorator",
            at = @At("HEAD"),
            cancellable = true)
    public void kiloChatDecorator(CallbackInfoReturnable<ChatDecorator> cir) {
        cir.setReturnValue(this.kiloChatDecorator);
    }

}
