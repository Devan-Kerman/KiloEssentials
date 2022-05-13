package org.kilocraft.essentials.mixin.patch.technical;

import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.server.dedicated.DedicatedServer;
import org.kilocraft.essentials.chat.KiloChatDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public abstract class DedicatedServerMixin {

    private final ChatDecorator kiloChatDecorator = new KiloChatDecorator();

    @Inject(
            method = "getChatDecorator",
            at = @At("HEAD"),
            cancellable = true)
    public void kiloChatDecorator(CallbackInfoReturnable<ChatDecorator> cir) {
        cir.setReturnValue(this.kiloChatDecorator);
    }

}
