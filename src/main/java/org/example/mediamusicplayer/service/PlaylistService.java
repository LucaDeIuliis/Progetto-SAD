package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;

public class PlaylistService {

    // Passiamo anche la libreria per poter controllare se il nome esiste già
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
}