package org.example.mediamusicplayer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Playlist {

    private final String id;
    private String name;
    private final List<Track> tracks;

    public Playlist(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.tracks = new ArrayList<>();
    }

    public Playlist(String id, String name, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.tracks = new ArrayList<>();

        if (tracks != null) {
            this.tracks.addAll(tracks);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Track> getTracks() {
        return new ArrayList<>(tracks);
    }

    public void setTracks(List<Track> tracks) {
        this.tracks.clear();

        if (tracks != null) {
            this.tracks.addAll(tracks);
        }
    }

    public int size() {
        return tracks.size();
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    @Override
    public String toString() {
        return name;
    }
}