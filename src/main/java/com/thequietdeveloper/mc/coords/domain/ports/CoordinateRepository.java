package com.thequietdeveloper.mc.coords.domain.ports;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;

import java.util.List;

public interface CoordinateRepository {
    void save(Coordinate coordinate);

    List<Coordinate> listCoordinates(int since);
}
