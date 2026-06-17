package org.example.mediamusicplayer.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:media_music_player.db";

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        try (var statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}