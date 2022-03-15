package com.thequietdeveloper.mc.coords.domain;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import com.thequietdeveloper.mc.coords.domain.models.Visibility;
import com.thequietdeveloper.mc.coords.domain.ports.CoordinateRepository;
import com.thequietdeveloper.mc.coords.domain.ports.PlayerCoordsNotifier;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CoordsService {
    private final CoordinateRepository repository;
    private final PlayerCoordsNotifier notifier;

    public CoordsService(CoordinateRepository repository, PlayerCoordsNotifier notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    public void saveCoordinate(Player player, String name) {
        this.saveCoordinate(player, name, Visibility.PRIVATE);
    }

    public void describeCoordinate(Player player, String name) {
        var coordinate = this.repository.get(player, name);
        if (coordinate.isEmpty()) {
            this.sendErrorToPlayer(player, "There's no coordinate named '" + name + "'");
            return;
        }
        this.notifier.describeCoordinate(player, coordinate.get());
    }

    public void saveCoordinate(Player player, String name, Visibility visibility) {
        if (name.isEmpty()) {
            this.sendErrorToPlayer(player, "You need to give a name to your coordinate");
            return;
        }

        if (this.repository.get(player, name).isPresent()) {
            this.sendErrorToPlayer(player, "There's a coordinate with the name '" + name + "' already");
            return;
        }

        var playerLocation = player.getLocation();
        var x = (int) playerLocation.getX();
        var y = (int) playerLocation.getY();
        var z = (int) playerLocation.getZ();
        var coordinate = new Coordinate(x, y, z, name, visibility, player.getWorld().getEnvironment(), player.getUniqueId().toString());
        this.repository.save(coordinate);
        this.notifier.notifySavedCoordinate(player, coordinate);
    }

    public void listCoordinatesForEnvironment(Player player, World.Environment environment) {
        var coordinates = this.repository.listCoordinates(player, environment);
        this.notifier.listCoordinates(player, coordinates, environment);
    }

    public void listAllCoordinates(Player player) {
        var coordinates = this.repository.listCoordinates(player);
        this.notifier.listCoordinates(player, coordinates);
    }

    public List<String> getCoordinateNames(Player player) {
        return this.repository.listCoordinates(player).stream().map(Coordinate::getName).sorted().collect(Collectors.toList());
    }

    public void sendErrorToPlayer(Player player, String error) {
        this.notifier.notifyError(player, error);
    }
}
