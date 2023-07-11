package com.slackow.backport.command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

public class DirCommand extends AbstractCommand {

    @Override
    public String getCommandName() {
        return "dir";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "commands.dir.usage";
    }

    @Override
    public void execute(CommandSource source, String[] args) {
        try {
            Desktop.getDesktop().open(MinecraftClient.getInstance().runDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException("commands.dir.failed");
        }
        run(source, this, "commands.dir.success");
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
