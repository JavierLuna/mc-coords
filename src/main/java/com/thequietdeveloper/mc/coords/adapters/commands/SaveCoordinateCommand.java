package com.thequietdeveloper.mc.coords.adapters.commands;

import com.thequietdeveloper.mc.coords.domain.CoordsService;
import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import com.thequietdeveloper.mc.coords.domain.ports.CoordsCommand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SaveCoordinateCommand implements CoordsCommand {
    CoordsService service;

    public SaveCoordinateCommand(CoordsService service) {
        this.service = service;
    }

    @Override
    public String getCommandId() {
        return "save";
    }

    @Override
    public boolean executeCommand(Player player, List<String> args) {
        var coordinateName = args.stream().collect(Collectors.joining(" "));
        this.service.saveCoordinate(player, coordinateName);
        return true;
    }

    @Override
    public List<String> tabComplete(Player player, List<String> args) {
        return null;
    }
}
