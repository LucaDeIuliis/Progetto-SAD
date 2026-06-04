package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;

public class PlaylistService {

    // --- CREAZIONE PLAYLIST ---
    public Playlist createPlaylist(String nome, MusicLibrary libreria) {

        // 1. Controllo che il nome non sia vuoto
        if (nome == null || nome.trim().isEmpty()) {
            throw new PlaylistValidationException("Nome mancante", "Per favore, inserisci un nome per la nuova playlist.");
        }

        String nomePulito = nome.trim();

        // 2. Controllo che non esista già una playlist con questo nome
        for (Playlist p : libreria.getPlaylists()) {
            if (p.getName().equalsIgnoreCase(nomePulito)) {
                throw new PlaylistValidationException("Playlist Duplicata", "Esiste già una playlist chiamata '" + nomePulito + "'. Scegli un nome diverso.");
            }
        }

        // 3. Se tutto va bene, creiamo e restituiamo la playlist
        return new Playlist(nomePulito);
    }

    // --- RINOMINA PLAYLIST ---
    public void renamePlaylist(Playlist playlist, String nuovoNome, MusicLibrary libreria) {

        if (playlist == null) {
            throw new PlaylistValidationException("Errore di Sistema", "Nessuna playlist selezionata.");
        }

        // 1. Controllo che il nuovo nome non sia vuoto
        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            throw new PlaylistValidationException("Nome mancante", "Il nome della playlist non può essere vuoto.");
        }

        String nomePulito = nuovoNome.trim();

        // 2. Controllo che non esista GIÀ un'ALTRA playlist con questo nuovo nome
        for (Playlist p : libreria.getPlaylists()) {
            // Saltiamo la playlist stessa che stiamo modificando (p != playlist)
            if (p != playlist && p.getName().equalsIgnoreCase(nomePulito)) {
                throw new PlaylistValidationException("Playlist Duplicata", "Esiste già una playlist chiamata '" + nomePulito + "'.");
            }
        }

        // 3. Se tutti i controlli sono passati, cambiamo il nome
        playlist.setName(nomePulito);
    }
}