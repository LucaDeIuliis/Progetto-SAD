package org.example.mediamusicplayer.repository;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

public class MusicLibraryRepository {

    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    public MusicLibraryRepository(TrackRepository trackRepository,
                                  PlaylistRepository playlistRepository) {
        this.trackRepository = trackRepository;
        this.playlistRepository = playlistRepository;
    }

    public void save(MusicLibrary library) {
        for (Track track : library.getAllTracks()) {
            trackRepository.save(track);
        }

        for (Playlist playlist : library.getPlaylists()) {
            playlistRepository.save(playlist);
            playlistRepository.savePlaylistTracks(playlist);
        }
    }
}