package com.thequietdeveloper.mc.coords.adapters.commands;

import com.thequietdeveloper.mc.coords.domain.CoordsService;
import com.thequietdeveloper.mc.coords.domain.ports.CoordsCommand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class GetCoordinateCommand implements CoordsCommand {
    CoordsService service;

    public GetCoordinateCommand(CoordsService service) {
        this.service = service;
    }

    @Override
    public String getCommandId() {
        return "";
    }

    @Override
    public boolean executeCommand(Player player, List<String> args) {
        var coordinateName = String.join(" ", args);
        this.service.describeCoordinate(player, coordinateName);
        return true;
    }

    @Override
    public List<String> tabComplete(Player player, List<String> args) {
        var coordinateNames = this.service.getCoordinateNames(player);
        if (args.isEmpty()) {
            return List.of();
        }
        var currentName = String.join(" ", args);
        return coordinateNames.stream().filter(coordinateName -> coordinateName.startsWith(currentName)).collect(Collectors.toList());
    }
}
