package org.example.mediamusicplayer.model;

import java.time.Duration;
import java.time.Year;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

public class Track {
    private String id;
    private String title;
    private String author;
    private Duration length; // Perfetto! Usiamo Duration
    private String genre;
    private Year year;

    // NUOVO: Insieme di tag visuali (senza duplicati)
    private Set<TrackTag> tags;

    // Numero di riproduzioni completate della traccia
    private int playCount;

    // Costruttore
    public Track(String title, String author, Duration length, String genre, Year year) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
        this.length = length;
        this.genre = genre;
        this.year = year;

        // NUOVO: Inizializza l'insieme dei tag come vuoto e ottimizzato per gli Enum
        this.tags = EnumSet.noneOf(TrackTag.class);
        this.playCount = 0;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void incrementPlayCount() {
        playCount++;
    }

    // --- GETTERS E SETTERS ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Duration getLength() { return length; }
    public void setLength(Duration length) { this.length = length; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Year getYear() { return year; }
    public void setYear(Year year) { this.year = year; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // === GESTIONE DEI TAG VISUALI ===

    public Set<TrackTag> getTags() {
        return tags;
    }

    public void addTag(TrackTag tag) {
        this.tags.add(tag);
    }

    public void removeTag(TrackTag tag) {
        this.tags.remove(tag);
    }

    /* * METODO PER LA TABELLA JAVAFX
     * Trasforma l'insieme di tag (es. [FAVOURITE, EXPLICIT])
     * in una stringa di emoji (es. "❤️ 🔞") pronta per lo schermo.
     */
    public String getVisualTags() {
        if (tags == null || tags.isEmpty()) return "";

        return tags.stream()
                .map(TrackTag::getSymbol)
                .collect(Collectors.joining(" "));
    }

    // --- METODO PER LA GRAFICA (JavaFX) ---
    // Guarda quanto è più pulito adesso grazie a Duration!
    public String getFormattedLength() {
        if (length == null) return "0:00";

        long minutes = length.toMinutes(); // Estrae i minuti totali
        int seconds = length.toSecondsPart(); // Estrae solo i secondi rimanenti (es. i 45 di "3:45")

        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return title + " - " + author + " [" + getFormattedLength() + "] (" + year + ")";
    }
}