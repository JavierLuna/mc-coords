package com.thequietdeveloper.mc.coords.domain.models;

import java.util.Optional;

public class Coordinate {
    private final Integer x;
    private final Integer z;
    private final Optional<Integer> y;
    private final String description;

    public Coordinate(Integer x, Integer z, String description) {
        this.x = x;
        this.z = z;
        this.y = Optional.empty();
        this.description = description;
    }

    public Coordinate(Integer x, Integer y, Integer z, String description) {
        this.x = x;
        this.y = Optional.of(y);
        this.z = z;
        this.description = description;
    }

    public Integer getX() {
        return x;
    }

    public Integer getZ() {
        return z;
    }

    public Optional<Integer> getY() {
        return y;
    }

    public String getDescription() {
        return description;
    }
}
