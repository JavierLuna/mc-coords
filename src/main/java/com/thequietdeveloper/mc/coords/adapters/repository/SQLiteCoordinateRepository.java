package com.thequietdeveloper.mc.coords.adapters.repository;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import com.thequietdeveloper.mc.coords.domain.models.Visibility;
import com.thequietdeveloper.mc.coords.domain.ports.CoordinateRepository;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class SQLiteCoordinateRepository implements CoordinateRepository {
    private static final String SQL_CREATE_COORDINATES_TABLE = "CREATE TABLE IF NOT EXISTS coordinates (" +
            "id integer PRIMARY KEY AUTOINCREMENT," +
            "x integer NOT NULL," +
            "y integer NOT NULL," +
            "z integer NOT NULL," +
            "name text NOT NULL," +
            "visibility text NOT NULL," +
            "environment text NOT NULL," +
            "ownerUUID text NOT NULL" +
            ");";

    private static final String SQL_INSERT_COORDINATE = "INSERT INTO coordinates(x,y,z,name,visibility,environment,ownerUUID) VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_SELECT_ALL_COORDINATES = "SELECT x, y, z, name, visibility, environment, ownerUUID FROM coordinates " +
            "WHERE (visibility = ? OR (visibility = ? AND ownerUUID = ?)) " +
            "ORDER BY environment, name DESC";
    private static final String SQL_SELECT_ENVIRONMENT_COORDINATES = "SELECT x, y, z, name, visibility, environment, ownerUUID FROM coordinates " +
            "WHERE environment = ? AND (visibility = ? OR (visibility = ? AND ownerUUID = ?)) " +
            "ORDER BY name DESC";
    private static final String SQL_GET_ENVIRONMENT_COORDINATES = "SELECT x, y, z, name, visibility, environment, ownerUUID FROM coordinates " +
            "WHERE name = ? AND environment = ? AND (visibility = ? OR (visibility = ? AND ownerUUID = ?)) " +
            "LIMIT 1";
    private static final Map<String, SQLiteCoordinateRepository> instances = new HashMap<>();

    private final Optional<Connection> connection;

    private SQLiteCoordinateRepository(String databaseName) {
        var databaseUri = String.format("jdbc:sqlite:%s", databaseName);
        this.connection = this.init(databaseUri);
    }

    private Connection getConnection(String databaseUri) throws SQLException {
        return DriverManager.getConnection(databaseUri);
    }

    private void createTables(Connection connection) throws SQLException {
        var stmt = connection.createStatement();
        stmt.execute(SQL_CREATE_COORDINATES_TABLE);
    }

    private Optional<Connection> init(String databaseUri) {
        try {
            var connection = getConnection(databaseUri);
            createTables(connection);
            return Optional.of(connection);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }

    public static SQLiteCoordinateRepository getInstance(String databaseName) {
        return instances.computeIfAbsent(databaseName, SQLiteCoordinateRepository::new);
    }

    @Override
    public Optional<Coordinate> get(Player player, String name) {
        Optional<Coordinate> coordinate = Optional.empty();
        if (this.connection.isPresent()) {
            try {
                var stmt = this.connection.get().prepareStatement(SQL_GET_ENVIRONMENT_COORDINATES);
                stmt.setString(1, name);
                stmt.setString(2, player.getWorld().getEnvironment().name());
                stmt.setString(3, Visibility.GLOBAL.name());
                stmt.setString(4, Visibility.PRIVATE.name());
                stmt.setString(5, player.getUniqueId().toString());
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    var x = rs.getInt(1);
                    var y = rs.getInt(2);
                    var z = rs.getInt(3);
                    var visibility = Visibility.valueOf(rs.getString(5));
                    var ownerUUID = rs.getString(7);

                    coordinate = Optional.of(new Coordinate(x, y, z, name, visibility, player.getWorld().getEnvironment(), ownerUUID));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return coordinate;
    }

    @Override
    public void save(Coordinate coordinate) {
        if (this.connection.isPresent()) {
            try {
                var pstmt = this.connection.get().prepareStatement(SQL_INSERT_COORDINATE);
                pstmt.setInt(1, coordinate.getX());
                pstmt.setInt(2, coordinate.getY());
                pstmt.setInt(3, coordinate.getZ());
                pstmt.setString(4, coordinate.getName());
                pstmt.setString(5, coordinate.getVisibility().name());
                pstmt.setString(6, coordinate.getEnvironment().name());
                pstmt.setString(7, coordinate.getOwnerUUID());
                pstmt.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public List<Coordinate> listCoordinates(Player player) {
        List<Coordinate> coordinates = new ArrayList<>();
        if (this.connection.isPresent()) {
            try {
                var stmt = this.connection.get().prepareStatement(SQL_SELECT_ALL_COORDINATES);
                stmt.setString(1, Visibility.GLOBAL.name());
                stmt.setString(2, Visibility.PRIVATE.name());
                stmt.setString(3, player.getUniqueId().toString());
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    var x = rs.getInt(1);
                    var y = rs.getInt(2);
                    var z = rs.getInt(3);
                    var name = rs.getString(4);
                    var visibility = Visibility.valueOf(rs.getString(5));
                    var environment = World.Environment.valueOf(rs.getString(6));
                    var ownerUUID = rs.getString(7);

                    coordinates.add(new Coordinate(x, y, z, name, visibility, environment, ownerUUID));

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return coordinates;
    }

    @Override
    public List<Coordinate> listCoordinates(Player player, World.Environment environment) {
        List<Coordinate> coordinates = new ArrayList<>();
        if (this.connection.isPresent()) {
            try {
                var stmt = this.connection.get().prepareStatement(SQL_SELECT_ENVIRONMENT_COORDINATES);
                stmt.setString(1, environment.name());
                stmt.setString(2, Visibility.GLOBAL.name());
                stmt.setString(3, Visibility.PRIVATE.name());
                stmt.setString(4, player.getUniqueId().toString());

                var rs = stmt.executeQuery();
                while (rs.next()) {
                    var x = rs.getInt(1);
                    var y = rs.getInt(2);
                    var z = rs.getInt(3);
                    var name = rs.getString(4);
                    var visibility = Visibility.valueOf(rs.getString(5));
                    var ownerUUID = rs.getString(7);

                    coordinates.add(new Coordinate(x, y, z, name, visibility, environment, ownerUUID));

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return coordinates;
    }
}
