package org.example.mediamusicplayer.repository;

import org.example.mediamusicplayer.persistence.DatabaseManager;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MusicLibraryRepository {

    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;
    private final DatabaseManager databaseManager;

    public MusicLibraryRepository(DatabaseManager databaseManager, TrackRepository trackRepository, PlaylistRepository playlistRepository) {
        this.databaseManager = databaseManager;
        this.trackRepository = trackRepository;
        this.playlistRepository = playlistRepository;
    }

    public void save(MusicLibrary library) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);

            try {
                for (Track track : library.getAllTracks()) {
                    trackRepository.save(connection, track);
                    trackRepository.saveTrackTags(connection, track);
                }

                for (Playlist playlist : library.getPlaylists()) {
                    playlistRepository.save(connection, playlist);
                    playlistRepository.savePlaylistTracks(connection, playlist);
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public MusicLibrary load() {
        List<Track> tracks = trackRepository.findAll();
        List<Playlist> playlists = playlistRepository.findAll();

        Map<String, Track> tracksById = new LinkedHashMap<>();
        for (Track track : tracks) {
            tracksById.put(track.getId(), track);
        }

        trackRepository.loadTrackTags(tracksById);

        Map<String, Playlist> playlistsById = new LinkedHashMap<>();
        for (Playlist playlist : playlists) {
            playlistsById.put(playlist.getId(), playlist);
        }

        playlistRepository.loadPlaylistTracks(
                playlistsById,
                tracksById
        );

        MusicLibrary library = new MusicLibrary();

        library.getAllTracks().addAll(tracks);
        library.getPlaylists().addAll(playlists);

        return library;
    }
}