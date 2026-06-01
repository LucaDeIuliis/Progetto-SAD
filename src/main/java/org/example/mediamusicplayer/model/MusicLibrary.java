package org.example.mediamusicplayer.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MusicLibrary {
    private ObservableList<Playlist> playlists;
    private ObservableList<Track> allTracks; // IL MAGAZZINO GLOBALE

    public MusicLibrary() {
        this.playlists = FXCollections.observableArrayList();
        this.allTracks = FXCollections.observableArrayList();
    }

    public ObservableList<Playlist> getPlaylists() { return playlists; }

    // Nuovo getter per la libreria globale
    public ObservableList<Track> getAllTracks() { return allTracks; }

    public void addPlaylist(Playlist playlist) {
        if (playlist != null && !playlists.contains(playlist)) {
            playlists.add(playlist);
        }
    }

    // Metodo per aggiungere canzoni al magazzino
    public void addTrackToLibrary(Track track) {
        if (track != null && !allTracks.contains(track)) {
            allTracks.add(track);
        }
    }
}