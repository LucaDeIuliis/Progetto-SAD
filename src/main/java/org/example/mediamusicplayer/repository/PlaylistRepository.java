package org.example.mediamusicplayer.repository;

import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlaylistRepository {

    private final DatabaseManager databaseManager;

    public PlaylistRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Playlist playlist) {
        String sql = """
                INSERT OR REPLACE INTO playlists (
                    id,
                    name,
                    generated_automatically,
                    filter_type,
                    automatic_filter,
                    play_count
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, playlist.getId());
            statement.setString(2, playlist.getName());
            statement.setInt(3, playlist.isGenerataAutomaticamente() ? 1 : 0);
            statement.setString(4, playlist.getTipoFiltro());
            statement.setString(5, playlist.getFiltroAutomatico());
            statement.setInt(6, playlist.getPlayCount());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della playlist", e);
        }
    }

    public void savePlaylistTracks(Playlist playlist) {
        String deleteSql = """
            DELETE FROM playlist_tracks
            WHERE playlist_id = ?
            """;

        String insertSql = """
            INSERT INTO playlist_tracks (
                playlist_id,
                track_id,
                track_order
            ) VALUES (?, ?, ?)
            """;

        try (Connection connection = databaseManager.getConnection()) {

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                deleteStatement.setString(1, playlist.getId());
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                for (int i = 0; i < playlist.getTracks().size(); i++) {
                    insertStatement.setString(1, playlist.getId());
                    insertStatement.setString(2, playlist.getTracks().get(i).getId());
                    insertStatement.setInt(3, i);
                    insertStatement.addBatch();
                }

                insertStatement.executeBatch();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio delle tracce della playlist", e);
        }
    }

    public void deleteById(String playlistId) {
        String deleteRelationsSql = """
            DELETE FROM playlist_tracks
            WHERE playlist_id = ?
            """;

        String deletePlaylistSql = """
            DELETE FROM playlists
            WHERE id = ?
            """;

        try (Connection connection = databaseManager.getConnection()) {

            try (PreparedStatement relationStatement =
                         connection.prepareStatement(deleteRelationsSql)) {

                relationStatement.setString(1, playlistId);
                relationStatement.executeUpdate();
            }

            try (PreparedStatement playlistStatement =
                         connection.prepareStatement(deletePlaylistSql)) {

                playlistStatement.setString(1, playlistId);
                playlistStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante l'eliminazione della playlist",
                    e
            );
        }
    }
}