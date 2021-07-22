package com.thequietdeveloper.mc.coords.adapters.commands;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import com.thequietdeveloper.mc.coords.domain.ports.CoordinateRepository;
import com.thequietdeveloper.mc.coords.domain.ports.ICoordsCommand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class AddCoordinateCommand implements ICoordsCommand {
    CoordinateRepository repository;

    public AddCoordinateCommand(CoordinateRepository repository) {
        this.repository = repository;
    }

    @Override
    public String getCommandId() {
        return "add";
    }

    public Optional<Integer> parseInteger(String arg) {
        try {
            return Optional.of(Integer.parseInt(arg));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }


    private Coordinate parseCoordinate(Player player, List<String> args) throws CoordinateParsingError {
        try {
            var possible0Coord = parseInteger(args.get(0));
            if (possible0Coord.isEmpty()) {
                var location = player.getLocation();
                return new Coordinate(location.getBlockX(), location.getBlockY(), location.getBlockZ(), String.join(" ", args));
            } else {
                var possible1Coord = parseInteger(args.get(1));
                if (possible1Coord.isEmpty()) {
                    throw new CoordinateParsingError();
                } else {
                    var possible2Coord = parseInteger(args.get(2));
                    if (possible2Coord.isEmpty()) {
                        return new Coordinate(possible0Coord.get(), possible1Coord.get(), String.join(" ", args.subList(2, args.size())));
                    } else {
                        return new Coordinate(possible0Coord.get(), possible1Coord.get(), possible2Coord.get(), String.join(" ", args.subList(3, args.size())));
                    }
                }
            }
        } catch (Exception e) {
            throw new CoordinateParsingError();
        }
    }

    @Override
    public boolean executeCommand(Player player, List<String> args) {
        try {
            var coordinate = parseCoordinate(player, args);
            repository.save(coordinate);
            player.sendMessage("Saved!");
        } catch (CoordinateParsingError coordinateParsingError) {
            player.sendMessage("Error parsing your coordinate");
            return false;
        }
        return true;
    }
}
