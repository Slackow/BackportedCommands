package com.slackow.backport.command;

import net.minecraft.command.AbstractCommand;
import net.minecraft.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandRegistryProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
public class FunctionCommand implements Command {

    public static final Map<String, List<String>> functionMap = new HashMap<>();

    public static File worldPath = null;

    public static boolean load(File saves) {
        worldPath = saves;
        File runDirectory = worldPath.getParentFile().getParentFile();
        File[] data = {saves, new File(saves, "functions"),
                runDirectory, new File(runDirectory, "functions")};

        functionMap.clear();
        Arrays.stream(data).map(File::listFiles)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(File::isFile)
                .filter(file -> file.getName().endsWith(".mcfunction"))
                .forEachOrdered(file -> {
                    try (Stream<String> lines = Files.lines(file.toPath())) {
                        String name = file.getName();
                        functionMap.put(name.substring(0, name.length() - 11),
                                lines.filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
                                        .collect(Collectors.toList()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return true;
    }

    public static boolean load() {
        return worldPath != null && load(worldPath);
    }


    @Override
    public String getCommandName() {
        return "function";
    }

    @Override
    public String getUsageTranslationKey(CommandSource source) {
        return "commands.function.usage";
    }

    @Override
    public List getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(CommandSource source, String[] args) {
        String fun = String.join(" ", args);
        List<String> commands = functionMap.get(fun);
        if (commands == null) {
            throw new CommandException("commands.function.failed", fun);
        }
        int successful = 0;
        List<String> failedCommands = new ArrayList<>(1);
        for (String command : commands) {
            CommandRegistryProvider var3 = MinecraftServer.getServer().getCommandManager();
            int res = var3.execute(source, command);
            if (res != 0) {
                successful++;
            } else {
                failedCommands.add(command);
            }
        }
        AbstractCommand.run(source, this, "commands.function.success", successful);
        if (commands.size() - successful > 0) {
            throw new CommandException("commands.function.x_failed", commands.size() - successful,
                    failedCommands.stream().map(s -> "\n'" + s + "'").collect(Collectors.joining()));
        }

    }

    @Override
    public boolean isAccessible(CommandSource source) {
        return true;
    }

    @Override
    public List method_3276(CommandSource source, String[] args) {
        String fun = String.join(" ", args);
        return functionMap.keySet().stream().filter(c -> c.startsWith(fun)).collect(Collectors.toList());
    }

    @Override
    public boolean isUsernameAtIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
