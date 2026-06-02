package org.example.mediamusicplayer.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
}