package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.Track;

import java.time.Duration;
import java.time.Year;

public class TrackService {

    public Track createTrack(String title, String author, String lengthStr, String genre, String yearStr) {

        // FASE 1: CONTROLLO CAMPI VUOTI
        if (title == null || title.trim().isEmpty()) {
            throw new TrackValidationException("Titolo mancante", "Per favore, inserisci il titolo della traccia.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new TrackValidationException("Autore mancante", "Per favore, inserisci l'autore della traccia.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new TrackValidationException("Genere mancante", "Per favore, inserisci il genere musicale.");
        }
        if (yearStr == null || yearStr.trim().isEmpty()) {
            throw new TrackValidationException("Anno mancante", "Per favore, inserisci l'anno di pubblicazione.");
        }
        if (lengthStr == null || lengthStr.trim().isEmpty()) {
            throw new TrackValidationException("Durata mancante", "Per favore, inserisci la durata della traccia.");
        }

        // FASE 2: GESTIONE ANNO
        Year year;
        try {
            year = Year.parse(yearStr);
        } catch (Exception e) {
            throw new TrackValidationException("Formato Anno Errato", "L'anno deve essere un numero valido (es. 2023).");
        }

        Year annoCorrente = Year.now();
        if (year.getValue() < 0) {
            throw new TrackValidationException("Anno negativo", "L'anno di pubblicazione non può essere negativo.");
        }
        if (year.isAfter(annoCorrente)) {
            throw new TrackValidationException("Anno dal futuro", "L'anno non può essere nel futuro! Inserisci un anno fino al " + annoCorrente.getValue() + ".");
        }

        // FASE 3: GESTIONE DURATA
        long totalSeconds = 0;
        try {
            if (lengthStr.contains(":")) {
                String[] parts = lengthStr.split(":");
                if (parts.length != 2) throw new NumberFormatException();

                long min = Long.parseLong(parts[0]);
                long sec = Long.parseLong(parts[1]);

                if (sec >= 60 || sec < 0 || min < 0) throw new NumberFormatException();
                totalSeconds = (min * 60) + sec;
            } else {
                totalSeconds = Long.parseLong(lengthStr);
            }
        } catch (Exception e) {
            throw new TrackValidationException("Formato Durata Errato",
                    "La durata deve essere in formato minuti:secondi(es. 3:45)\noppure in secondi totali (es. 225).\n\nAssicurati di non aver inserito lettere o simboli\ne che i minuti e i secondi siano compresi tra 0-59");
        }

        if (totalSeconds < 1) {
            throw new TrackValidationException("Durata non valida", "La durata della traccia non può essere zero o negativa!");
        }

        Duration length = Duration.ofSeconds(totalSeconds);

        // FASE 4: RESTITUZIONE DELLA TRACCIA CREATA
        return new Track(title, author, length, genre, year);
    }
}