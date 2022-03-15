package com.thequietdeveloper.mc.coords.domain.ports;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface PlayerCoordsNotifier {
    void listCoordinates(Player player, List<Coordinate> coordinates);

    void listCoordinates(Player player, List<Coordinate> coordinates, World.Environment environment);

    void describeCoordinate(Player player, Coordinate coordinate);

    void notifySavedCoordinate(Player player, Coordinate coordinate);

    void notifyError(Player player, String errorMessage);
}
