package com.thequietdeveloper.mc.coords.adapters.commands;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import com.thequietdeveloper.mc.coords.domain.ports.CoordinateRepository;
import com.thequietdeveloper.mc.coords.domain.ports.ICoordsCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCoordinatesCommand implements ICoordsCommand {
    CoordinateRepository repository;

    public ListCoordinatesCommand(CoordinateRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getCommandId() {
        return "list";
    }

    private int getStartingPage(List<String> args) {
        return 0;
    }

    private String getCoordinateListMessage(Coordinate coordinate) {
        String listMessage;
        if (coordinate.getY().isPresent()) {
            listMessage = String.format("   - %d %d %d -> %s", coordinate.getX(), coordinate.getY().get(), coordinate.getZ(), coordinate.getDescription());
        } else {
            listMessage = String.format("   - %d %d -> %s", coordinate.getX(), coordinate.getZ(), coordinate.getDescription());
        }
        return listMessage;
    }

    @Override
    public boolean executeCommand(Player player, List<String> args) {
        int startingPage = getStartingPage(args);
        var coordinates = this.repository.listCoordinates(startingPage);

        if (!coordinates.isEmpty()) {
            player.sendMessage("Coordinates:");
            for (Coordinate coordinate : coordinates) {
                player.sendMessage(getCoordinateListMessage(coordinate));
            }
        } else {
            player.sendMessage("You have no coordinates saved!");
        }
        return true;
    }
}
