package org.example.mediamusicplayer.repository;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MusicLibraryRepository {

    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    public MusicLibraryRepository(
            TrackRepository trackRepository,
            PlaylistRepository playlistRepository
    ) {
        this.trackRepository = trackRepository;
        this.playlistRepository = playlistRepository;
    }

    public void save(MusicLibrary library) {
        for (Track track : library.getAllTracks()) {
            trackRepository.save(track);
            trackRepository.saveTrackTags(track);
        }

        for (Playlist playlist : library.getPlaylists()) {
            playlistRepository.save(playlist);
            playlistRepository.savePlaylistTracks(playlist);
        }
    }

    public MusicLibrary load() {
        List<Track> tracks = trackRepository.findAll();
        List<Playlist> playlists = playlistRepository.findAll();

        Map<String, Track> tracksById = new LinkedHashMap<>();

        for (Track track : tracks) {
            tracksById.put(track.getId(), track);
        }

        // Ripristina i tag associati alle tracce caricate.
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