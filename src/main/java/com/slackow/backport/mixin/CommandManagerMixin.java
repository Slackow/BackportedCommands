package com.slackow.backport.mixin;

import com.slackow.backport.command.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin extends CommandRegistry {
	@Inject(method = "<init>", at = @At("TAIL"))
	public void registerBackportedCommands(CallbackInfo ci){
		registerCommand(new FillCommand());
		registerCommand(new CloneCommand());
		registerCommand(new FunctionCommand());
		registerCommand(new ReloadCommand());
		registerCommand(new DirCommand());
	}
}
