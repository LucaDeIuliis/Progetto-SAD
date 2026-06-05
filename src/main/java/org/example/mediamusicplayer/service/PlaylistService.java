package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

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