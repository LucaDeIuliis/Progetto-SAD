package org.example.mediamusicplayer.repository;

import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TrackRepository {

    private final DatabaseManager databaseManager;

    public TrackRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Track track) {
        String sql = """
                INSERT OR REPLACE INTO tracks (
                    id,
                    title,
                    author,
                    length_seconds,
                    genre,
                    year,
                    play_count
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, track.getId());
            statement.setString(2, track.getTitle());
            statement.setString(3, track.getAuthor());
            statement.setLong(4, track.getLength().getSeconds());
            statement.setString(5, track.getGenre());
            statement.setInt(6, track.getYear().getValue());
            statement.setInt(7, track.getPlayCount());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della traccia", e);
        }
    }

    public void deleteById(String trackId) {
        String deleteRelationsSql = """
            DELETE FROM playlist_tracks
            WHERE track_id = ?
            """;

        String deleteTrackSql = """
            DELETE FROM tracks
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection()) {

            try (PreparedStatement relationStatement =
                         connection.prepareStatement(deleteRelationsSql)) {

                relationStatement.setString(1, trackId);
                relationStatement.executeUpdate();
            }

            try (PreparedStatement trackStatement =
                         connection.prepareStatement(deleteTrackSql)) {

                trackStatement.setString(1, trackId);
                trackStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante l'eliminazione della traccia",
                    e
            );
        }
    }
}