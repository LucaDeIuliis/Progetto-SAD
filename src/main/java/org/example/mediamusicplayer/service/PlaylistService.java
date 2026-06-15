package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.model.TrackTag;

public class PlaylistService {

    // --- CREAZIONE PLAYLIST ---
    public Playlist createPlaylist(String nome, MusicLibrary libreria) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new PlaylistValidationException(
                    "Nome mancante",
                    "Per favore, inserisci un nome per la nuova playlist."
            );
        }

        String nomePulito = nome.trim();

        for (Playlist p : libreria.getPlaylists()) {
            if (p.getName().equalsIgnoreCase(nomePulito)) {
                throw new PlaylistValidationException(
                        "Playlist Duplicata",
                        "Esiste già una playlist chiamata '" + nomePulito + "'. Scegli un nome diverso."
                );
            }
        }

        return new Playlist(nomePulito);
    }

    // --- RINOMINA PLAYLIST ---
    public void renamePlaylist(Playlist playlist, String nuovoNome, MusicLibrary libreria) {

        if (playlist == null) {
            throw new PlaylistValidationException(
                    "Errore di Sistema",
                    "Nessuna playlist selezionata."
            );
        }

        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            throw new PlaylistValidationException(
                    "Nome mancante",
                    "Il nome della playlist non può essere vuoto."
            );
        }

        String nomePulito = nuovoNome.trim();

        for (Playlist p : libreria.getPlaylists()) {
            if (p != playlist && p.getName().equalsIgnoreCase(nomePulito)) {
                throw new PlaylistValidationException(
                        "Playlist Duplicata",
                        "Esiste già una playlist chiamata '" + nomePulito + "'."
                );
            }
        }

        playlist.setName(nomePulito);
    }

    // --- AGGIUNTA TRACCIA A PLAYLIST ---
    public void addTrackToPlaylist(Playlist playlist, Track track) {

        if (playlist == null) {
            throw new PlaylistValidationException(
                    "Nessuna Playlist",
                    "Seleziona una playlist valida."
            );
        }

        if (track == null) {
            throw new PlaylistValidationException(
                    "Nessuna Traccia",
                    "Seleziona una traccia valida."
            );
        }

        if (playlist.getTracks().contains(track)) {
            throw new PlaylistValidationException(
                    "Già presente",
                    "Questa traccia è già presente nella playlist."
            );
        }

        playlist.addTrack(track);
    }

    // =========================================================
    // LOGICA DI BUSINESS: SMART PLAYLIST AUTO-SYNC
    // =========================================================
    public void syncSmartPlaylists(MusicLibrary library, MusicLibraryService libraryService) {
        for (TrackTag tag : TrackTag.values()) {
            String nomeBase = switch (tag) {
                case FAVOURITE -> "I Miei Preferiti";
                case EXPLICIT -> "Brani Espliciti";
                case NEW_RELEASE -> "Nuove Uscite";
            };
            String nomeCompleto = nomeBase + " " + tag.getSymbol();

            // 1. Filtra le tracce
            java.util.List<Track> tracceTaggate = library.getAllTracks().stream()
                    .filter(track -> track.getTags().contains(tag))
                    .toList();

            // 2. Cerca la playlist
            Playlist playlistEsistente = library.getPlaylists().stream()
                    .filter(p -> p.getName().equals(nomeCompleto))
                    .findFirst()
                    .orElse(null);

            // 3. Aggiorna o Crea
            if (playlistEsistente != null) {
                playlistEsistente.getTracks().clear();
                playlistEsistente.getTracks().addAll(tracceTaggate);
            } else if (!tracceTaggate.isEmpty()) {
                Playlist nuovaAuto = new Playlist(nomeCompleto);
                nuovaAuto.getTracks().addAll(tracceTaggate);
                libraryService.addPlaylist(library, nuovaAuto);
            }
        }
    }


    // --- RIMOZIONE TRACCIA DA PLAYLIST ---
    public void removeTrackFromPlaylist(Playlist playlist, Track track) {

        if (playlist == null) {
            throw new PlaylistValidationException(
                    "Nessuna Playlist",
                    "Seleziona una playlist valida."
            );
        }

        if (track == null) {
            throw new PlaylistValidationException(
                    "Nessuna Traccia",
                    "Seleziona una traccia valida."
            );
        }

        if (!playlist.getTracks().contains(track)) {
            throw new PlaylistValidationException(
                    "Traccia non presente",
                    "La traccia selezionata non è presente nella playlist."
            );
        }

        playlist.removeTrack(track);
    }
}