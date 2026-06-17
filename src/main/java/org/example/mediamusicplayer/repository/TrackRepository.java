package org.example.mediamusicplayer.repository;

import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.model.TrackTag;
import org.example.mediamusicplayer.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrackRepository {

    private final DatabaseManager databaseManager;

    public TrackRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Track track) {
        String sql = """
                INSERT INTO tracks (
                    id,
                    title,
                    author,
                    length_seconds,
                    genre,
                    year,
                    play_count
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    title = excluded.title,
                    author = excluded.author,
                    length_seconds = excluded.length_seconds,
                    genre = excluded.genre,
                    year = excluded.year,
                    play_count = excluded.play_count
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
            throw new RuntimeException(
                    "Errore durante il salvataggio della traccia",
                    e
            );
        }
    }

    public void deleteById(String trackId) {
        String deleteRelationsSql = """
                DELETE FROM playlist_tracks
                WHERE track_id = ?
                """;

        String deleteTagsSql = """
                DELETE FROM track_tags
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

            try (PreparedStatement tagsStatement =
                         connection.prepareStatement(deleteTagsSql)) {

                tagsStatement.setString(1, trackId);
                tagsStatement.executeUpdate();
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

    public List<Track> findAll() {
        String sql = """
                SELECT
                    id,
                    title,
                    author,
                    length_seconds,
                    genre,
                    year,
                    play_count
                FROM tracks
                ORDER BY rowid
                """;

        List<Track> tracks = new ArrayList<>();

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Track track = new Track(
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        Duration.ofSeconds(
                                resultSet.getLong("length_seconds")
                        ),
                        resultSet.getString("genre"),
                        Year.of(resultSet.getInt("year"))
                );

                track.setId(resultSet.getString("id"));
                track.setPlayCount(resultSet.getInt("play_count"));

                tracks.add(track);
            }

            return tracks;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante il caricamento delle tracce",
                    e
            );
        }
    }

    public void saveTrackTags(Track track) {
        String deleteSql = """
                DELETE FROM track_tags
                WHERE track_id = ?
                """;

        String insertSql = """
                INSERT INTO track_tags (
                    track_id,
                    tag
                )
                VALUES (?, ?)
                """;

        try (Connection connection = databaseManager.getConnection()) {

            try (PreparedStatement deleteStatement =
                         connection.prepareStatement(deleteSql)) {

                deleteStatement.setString(1, track.getId());
                deleteStatement.executeUpdate();
            }

            try (PreparedStatement insertStatement =
                         connection.prepareStatement(insertSql)) {

                for (TrackTag tag : track.getTags()) {
                    insertStatement.setString(1, track.getId());
                    insertStatement.setString(2, tag.name());
                    insertStatement.addBatch();
                }

                insertStatement.executeBatch();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante il salvataggio dei tag della traccia",
                    e
            );
        }
    }

    public void loadTrackTags(Map<String, Track> tracksById) {
        String sql = """
                SELECT track_id, tag
                FROM track_tags
                ORDER BY track_id, tag
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Track track = tracksById.get(
                        resultSet.getString("track_id")
                );

                if (track == null) {
                    continue;
                }

                try {
                    TrackTag tag = TrackTag.valueOf(
                            resultSet.getString("tag")
                    );

                    track.addTag(tag);

                } catch (IllegalArgumentException ignored) {
                    // Ignora valori non più compatibili con l'enum.
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Errore durante il caricamento dei tag delle tracce",
                    e
            );
        }
    }
}