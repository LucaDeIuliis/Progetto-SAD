package org.example.mediamusicplayer.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
    private String name;
    private ObservableList<Track> tracks;

    public Playlist(String name) {
        this.name = name;
        this.tracks = FXCollections.observableArrayList();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ObservableList<Track> getTracks() { return tracks; }

    public void addTrack(Track track) { this.tracks.add(track); }
    public void removeTrack(Track track) { this.tracks.remove(track); }

    @Override
    public String toString() {
        return name;
    }
}