package com.slackow.backport.mixin;

import com.slackow.backport.resource.ModResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private List<ResourcePack> resourcePacks;

    @Inject(method = "method_5574", at = @At("HEAD"))
    private void addResourcePacks(CallbackInfo ci) {
        ModContainer mod = FabricLoader.getInstance().getModContainer("backportedcommands").orElseThrow(RuntimeException::new);
        String modId = mod.getMetadata().getId();
        resourcePacks.add(new ModResourcePack(modId, mod));
    }
}

