package org.example.mediamusicplayer.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private final DatabaseManager databaseManager;

    public DatabaseInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initializeDatabase() {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS tracks (
                        id TEXT PRIMARY KEY,
                        title TEXT NOT NULL,
                        author TEXT NOT NULL,
                        length_seconds INTEGER NOT NULL,
                        genre TEXT,
                        year INTEGER,
                        play_count INTEGER NOT NULL DEFAULT 0
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS playlists (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        generated_automatically INTEGER NOT NULL DEFAULT 0,
                        filter_type TEXT,
                        automatic_filter TEXT,
                        play_count INTEGER NOT NULL DEFAULT 0
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS playlist_tracks (
                        playlist_id TEXT NOT NULL,
                        track_id TEXT NOT NULL,
                        track_order INTEGER NOT NULL,
                        PRIMARY KEY (playlist_id, track_id),
                        FOREIGN KEY (playlist_id) REFERENCES playlists(id),
                        FOREIGN KEY (track_id) REFERENCES tracks(id)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS track_tags (
                        track_id TEXT NOT NULL,
                        tag TEXT NOT NULL,
                        PRIMARY KEY (track_id, tag),
                        FOREIGN KEY (track_id) REFERENCES tracks(id)
                    )
                    """);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante l'inizializzazione del database",
                    e
            );
        }
    }
}