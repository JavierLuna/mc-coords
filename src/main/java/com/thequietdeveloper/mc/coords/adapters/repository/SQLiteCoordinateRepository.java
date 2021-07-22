package com.thequietdeveloper.mc.coords.adapters.repository;

import com.thequietdeveloper.mc.coords.domain.models.Coordinate;
import com.thequietdeveloper.mc.coords.domain.ports.CoordinateRepository;

import java.sql.*;
import java.util.*;

public class SQLiteCoordinateRepository implements CoordinateRepository {
    private static final String SQL_CREATE_COORDINATES_TABLE = "CREATE TABLE IF NOT EXISTS coordinates (" +
            "id integer PRIMARY KEY AUTOINCREMENT," +
            "x integer NOT NULL," +
            "y integer," +
            "z integer NOT NULL," +
            "description text NOT NULL" +
            ");";

    private static final String SQL_INSERT_COORDINATE = "INSERT INTO coordinates(x,y,z,description) VALUES(?,?,?,?)";
    private static final String SQL_SELECT_COORDINATES = "SELECT x, y, z, description FROM coordinates";
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
        Statement stmt = connection.createStatement();
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
    public void save(Coordinate coordinate) {
        if (this.connection.isPresent()) {
            try {
                PreparedStatement pstmt = this.connection.get().prepareStatement(SQL_INSERT_COORDINATE);
                pstmt.setInt(1, coordinate.getX());
                if (coordinate.getY().isPresent()) {
                    pstmt.setInt(2, coordinate.getY().get());
                } else {
                    pstmt.setNull(2, java.sql.Types.INTEGER);
                }
                pstmt.setInt(3, coordinate.getZ());
                pstmt.setString(4, coordinate.getDescription());
                pstmt.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public List<Coordinate> listCoordinates(int since) {
        List<Coordinate> coordinates = new ArrayList<>();
        if (this.connection.isPresent()) {
            try {
                Statement stmt = this.connection.get().createStatement();
                ResultSet rs = stmt.executeQuery(SQL_SELECT_COORDINATES);
                while (rs.next()) {
                    Integer x = rs.getInt(1);
                    Integer z = rs.getInt(3);
                    String description = rs.getString(4);

                    Integer y = rs.getInt(2);
                    if (rs.wasNull()) {
                        coordinates.add(new Coordinate(x, z, description));
                    } else {
                        coordinates.add(new Coordinate(x, y, z, description));
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return coordinates;
    }
}
