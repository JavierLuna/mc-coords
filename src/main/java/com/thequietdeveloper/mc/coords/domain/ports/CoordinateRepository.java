package com.thequietdeveloper.mc.coords.domain.ports;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public interface CoordinateRepository {

    Optional<Coordinate> get(Player player, String name);

    void save(Coordinate coordinate);

    List<Coordinate> listCoordinates(Player player);

    List<Coordinate> listCoordinates(Player player, World.Environment world);
}
