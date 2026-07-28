package me.pajic.modid.mixin.client;

import me.pajic.modid.ModId;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ExampleClientMixin {

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void init(CallbackInfo ci) {
        ModId.debugLog("The client mixin is alive!");
    }
}
