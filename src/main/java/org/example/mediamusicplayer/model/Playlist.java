package org.example.mediamusicplayer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Playlist {

    private final String id;
    private String name;
    private final List<Track> tracks;

    // Costruttore
    public Playlist(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.tracks = new ArrayList<>();
    }

    // Costruttore completo (opzionale)
    public Playlist(String id, String name, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.tracks = new ArrayList<>(tracks);
    }

    // --- GETTERS E SETTERS ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Restituisce una copia per evitare modifiche esterne
    public List<Track> getTracks() {
        return new ArrayList<>(tracks);
    }

    // --- METODI DEL MODELLO ---

    public void addTrack(Track track) {
        if (track != null) {
            tracks.add(track);
        }
    }

    public boolean removeTrack(Track track) {
        return tracks.remove(track);
    }

    public void reorderTrack(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= tracks.size()
                || toIndex < 0 || toIndex >= tracks.size()) {
            throw new IndexOutOfBoundsException("Indice non valido");
        }

        Collections.swap(tracks, fromIndex, toIndex);
    }

    public int size() {
        return tracks.size();
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    @Override
    public String toString() {
        return name + " (" + tracks.size() + " tracks)";
    }
}

