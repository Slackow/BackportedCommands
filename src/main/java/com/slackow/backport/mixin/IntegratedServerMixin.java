package com.slackow.backport.mixin;

import com.slackow.backport.command.FunctionCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.net.Proxy;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin extends MinecraftServer {
    @Shadow protected abstract File getRunDirectory();

    @Shadow @Final private MinecraftClient client;

    public IntegratedServerMixin(File file, Proxy proxy) {
        super(file, proxy);
    }

    @Inject(method = "setupWorld()V", at = @At("HEAD"))
    public void a(CallbackInfo ci) {
        File saves = new File(new File(getRunDirectory(), "saves"), this.getLevelName());

        FunctionCommand.load(saves);
    }
}
