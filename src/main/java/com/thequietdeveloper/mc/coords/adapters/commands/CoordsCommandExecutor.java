package com.thequietdeveloper.mc.coords.adapters.commands;

import com.thequietdeveloper.mc.coords.domain.ports.CoordsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoordsCommandExecutor implements CommandExecutor, TabCompleter {
    private final HashMap<String, CoordsCommand> registeredCommands;
    private CoordsCommand fallbackCommand;

    public CoordsCommandExecutor() {
        super();
        this.registeredCommands = new HashMap<>();
    }

    public void registerCommand(CoordsCommand command) {
        this.registeredCommands.put(command.getCommandId(), command);
    }

    public void registerFallbackCommand(CoordsCommand command) {
        this.fallbackCommand = command;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command needs to be executed by a player");
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage("Usage:"); // TODO
            return true;
        }
        return this.getCommand(args[0]).map(cmd -> this.executeCommand(cmd, player, Arrays.asList(args))).orElse(false);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        var commandId = args[0];
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Command needs to be executed by a player");
            return null;
        }
        var coordsCommand = this.getCommand(commandId);
        return coordsCommand.map(cmd -> this.tabCompleteCommand(cmd, player, Arrays.asList(args))).orElse(args.length == 1 ? this.getCommandIds() : List.of());
    }

    private Optional<CoordsCommand> getCommand(String commandId) {
        if (this.registeredCommands.containsKey(commandId)) {
            return Optional.of(this.registeredCommands.get(commandId));
        }
        return Optional.of(this.fallbackCommand);
    }

    private boolean executeCommand(CoordsCommand command, Player player, List<String> args) {
        return command.executeCommand(player, this.getArgsForCommand(command, args));
    }

    private List<String> tabCompleteCommand(CoordsCommand command, Player player, List<String> args) {
        if (command == fallbackCommand) {
            var fallbackCommandTabComplete = fallbackCommand.tabComplete(player, args);
            if (fallbackCommandTabComplete == null) fallbackCommandTabComplete = List.of();
            var currentSuggestions = Stream.concat(this.getCommandIds().stream(), fallbackCommandTabComplete.stream()).toList();
            if (args.isEmpty()) {
                return currentSuggestions;
            }
            var currentCommand = String.join(" ", args);
            return currentSuggestions.stream().filter(suggestion -> suggestion.startsWith(currentCommand)).collect(Collectors.toList());
        }
        return command.tabComplete(player, this.getArgsForCommand(command, args));
    }

    private List<String> getCommandIds() {
        return this.registeredCommands.keySet().stream().sorted().collect(Collectors.toList());
    }

    private List<String> getArgsForCommand(CoordsCommand command, List<String> args) {
        return command == this.fallbackCommand ? args : args.subList(1, args.size());
    }

}
