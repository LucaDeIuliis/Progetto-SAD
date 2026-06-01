package org.example.mediamusicplayer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Playlist {

    private final String id;
    private String name;
    private final List<Track> tracks;

    // Costruttore per creare una nuova playlist
    public Playlist(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.tracks = new ArrayList<>();
    }

    // Costruttore completo, utile in futuro per persistenza/caricamento dati
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

    // Restituisce una copia della lista,
    // così dall'esterno non si modifica direttamente la lista interna
    public List<Track> getTracks() {
        return new ArrayList<>(tracks);
    }

    // --- METODI DEL MODELLO ---

    public void addTrack(Track track) {
        tracks.add(track);
    }

    public boolean removeTrack(Track track) {
        return tracks.remove(track);
    }

    public boolean containsTrack(Track track) {
        return tracks.contains(track);
    }

    public int getNumberOfTracks() {
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