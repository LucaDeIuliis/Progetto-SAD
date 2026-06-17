package org.example.mediamusicplayer.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MusicLibrary {
    private ObservableList<Playlist> playlists;
    private ObservableList<Track> allTracks;

    public MusicLibrary() {
        this.playlists = FXCollections.observableArrayList();
        this.allTracks = FXCollections.observableArrayList();
    }

    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }

    public ObservableList<Track> getAllTracks() {
        return allTracks;
    }

    public MusicLibrary copy() {
        MusicLibrary copy = new MusicLibrary();

        Map<String, Track> copiedTracksById = new LinkedHashMap<>();

        for (Track track : getAllTracks()) {
            Track copiedTrack = track.copy();

            copiedTracksById.put(
                    copiedTrack.getId(),
                    copiedTrack
            );

            copy.getAllTracks().add(copiedTrack);
        }

        for (Playlist playlist : getPlaylists()) {
            Playlist copiedPlaylist =
                    playlist.copy(copiedTracksById);

            copy.getPlaylists().add(copiedPlaylist);
        }

        return copy;
    }
}