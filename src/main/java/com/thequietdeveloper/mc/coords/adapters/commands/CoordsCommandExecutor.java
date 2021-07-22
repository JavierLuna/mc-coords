package com.thequietdeveloper.mc.coords.adapters.commands;

import com.thequietdeveloper.mc.coords.domain.ports.ICoordsCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CoordsCommandExecutor implements CommandExecutor {
    private HashMap<String, ICoordsCommand> registeredCommands;

    public CoordsCommandExecutor() {
        super();
        this.registeredCommands = new HashMap<>();
    }

    public void registerCommand(ICoordsCommand command) {
        this.registeredCommands.put(command.getCommandId(), command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Command needs to be executed by a player");
            return false;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage("Usage:");
            return true;
        }
        String commandId = args[0];
        if (this.registeredCommands.containsKey(commandId)) {
            return executeCommand(commandId, player, Arrays.asList(args).subList(1, args.length));
        } else {
            player.sendMessage(String.format("Command '%s' does not exist", commandId));
            return false;
        }
    }

    private boolean executeCommand(String commandId, Player player, List<String> args) {
        ICoordsCommand command = this.registeredCommands.get(commandId);
        return command.executeCommand(player, args);
    }
}
