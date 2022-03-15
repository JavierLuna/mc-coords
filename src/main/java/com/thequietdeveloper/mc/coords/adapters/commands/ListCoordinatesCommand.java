package com.thequietdeveloper.mc.coords.adapters.commands;

import com.thequietdeveloper.mc.coords.domain.CoordsService;
import com.thequietdeveloper.mc.coords.domain.ports.CoordsCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListCoordinatesCommand implements CoordsCommand {
    private final CoordsService service;

    public ListCoordinatesCommand(CoordsService service) {
        this.service = service;
    }

    @Override
    public String getCommandId() {
        return "list";
    }

    @Override
    public boolean executeCommand(Player player, List<String> args) {
        if (args.isEmpty()) {
            this.service.listCoordinatesForEnvironment(player, player.getWorld().getEnvironment());
        } else if (args.size() == 1) {
            var arg = args.get(0);
            switch (arg) {
                case "all" -> this.service.listAllCoordinates(player);
                case "world" -> this.service.listCoordinatesForEnvironment(player, World.Environment.NORMAL);
                case "nether" -> this.service.listCoordinatesForEnvironment(player, World.Environment.NETHER);
                case "end" -> this.service.listCoordinatesForEnvironment(player, World.Environment.THE_END);
                case "custom" -> this.service.listCoordinatesForEnvironment(player, World.Environment.CUSTOM);
                default -> this.service.sendErrorToPlayer(player, "Invalid argument. Possible arguments are 'all', 'world', 'nether', 'end' or 'custom'");
            }

        } else {
            this.service.sendErrorToPlayer(player, "Invalid arguments. Possible arguments are 'all', 'world', 'nether', 'end' or 'custom'");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(Player player, List<String> args) {
        Bukkit.getLogger().info(String.join(",", args));
        Bukkit.getLogger().info(args.isEmpty() + "");
        var allOptions = Arrays.asList("all", "nether", "end", "world", "custom");

        if (args.isEmpty()) {
            return List.of();
        } else if (args.size() == 1) {
            return allOptions.stream().filter(option -> option.startsWith(args.get(0))).collect(Collectors.toList());
        }
        return null;
    }
}
