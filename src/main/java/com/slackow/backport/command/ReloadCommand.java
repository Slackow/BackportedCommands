package com.slackow.backport.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends AbstractCommand {
    @Override
    public String getCommandName() {
        return "reload";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "commands.reload.usage";
    }

    @Override
    public void execute(CommandSource source, String[] args) {
        if (args.length != 0) {
            throw new CommandException(getUsageTranslationKey(source));
        }
        if (FunctionCommand.load()) {
            run(source, this, "commands.reload.success");
        } else {
            throw new CommandException("commands.reload.failed");
        }
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
