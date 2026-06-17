package org.example.mediamusicplayer.repository;

import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.persistence.DatabaseManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.example.mediamusicplayer.model.Track;
import java.util.Map;

public class PlaylistRepository {

    private final DatabaseManager databaseManager;

    public PlaylistRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Playlist playlist) {
        String sql = """
                INSERT INTO playlists (
                    id,
                    name,
                    generated_automatically,
                    filter_type,
                    automatic_filter,
                    play_count
                )
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    name = excluded.name,
                    generated_automatically = excluded.generated_automatically,
                    filter_type = excluded.filter_type,
                    automatic_filter = excluded.automatic_filter,
                    play_count = excluded.play_count
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

    public List<Playlist> findAll() {
        String sql = """
            SELECT
                id,
                name,
                generated_automatically,
                filter_type,
                automatic_filter,
                play_count
            FROM playlists
            ORDER BY rowid
            """;

        List<Playlist> playlists = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Playlist playlist = new Playlist(
                        resultSet.getString("name")
                );

                playlist.setId(resultSet.getString("id"));
                playlist.setGenerataAutomaticamente(
                        resultSet.getInt("generated_automatically") == 1
                );
                playlist.setTipoFiltro(resultSet.getString("filter_type"));
                playlist.setFiltroAutomatico(resultSet.getString("automatic_filter"));
                playlist.setPlayCount(resultSet.getInt("play_count"));

                playlists.add(playlist);
            }

            return playlists;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante il caricamento delle playlist",
                    e
            );
        }
    }

    public void loadPlaylistTracks(
            Map<String, Playlist> playlistsById,
            Map<String, Track> tracksById
    ) {
        String sql = """
            SELECT
                playlist_id,
                track_id
            FROM playlist_tracks
            ORDER BY playlist_id, track_order
            """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Playlist playlist =
                        playlistsById.get(resultSet.getString("playlist_id"));

                Track track =
                        tracksById.get(resultSet.getString("track_id"));

                if (playlist != null && track != null) {
                    playlist.getTracks().add(track);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante il caricamento delle tracce delle playlist",
                    e
            );
        }
    }
}