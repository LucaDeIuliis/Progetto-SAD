package org.example.mediamusicplayer.model;

import java.time.Duration;
import java.time.Year;

public class Track {

    private String title;
    private String author;
    private Duration length; // Perfetto! Usiamo Duration
    private String genre;
    private Year year;

    // Costruttore
    public Track(String title, String author, Duration length, String genre, Year year) {
        this.title = title;
        this.author = author;
        this.length = length;
        this.genre = genre;
        this.year = year;
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