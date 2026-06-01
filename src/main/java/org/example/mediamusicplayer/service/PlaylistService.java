package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

import java.util.List;

public class PlaylistService {

    public Playlist createPlaylist(String name, List<Playlist> existingPlaylists) {

        if (name == null || name.trim().isEmpty()) {
            throw new PlaylistValidationException(
                    "Nome playlist mancante",
                    "Per favore, inserisci il nome della playlist."
            );
        }

        String cleanName = name.trim();

        if (existingPlaylists != null) {
            for (Playlist playlist : existingPlaylists) {
                if (playlist.getName().equalsIgnoreCase(cleanName)) {
                    throw new PlaylistValidationException(
                            "Playlist già esistente",
                            "Esiste già una playlist con questo nome."
                    );
                }
            }
        }

        return new Playlist(cleanName);
    }

    public void deletePlaylist(Playlist selectedPlaylist) {

        if (selectedPlaylist == null) {
            throw new PlaylistValidationException(
                    "Nessuna playlist selezionata",
                    "Per favore, seleziona una playlist prima di cliccare su Elimina."
            );
        }
    }

    public void addTrackToPlaylist(Playlist playlist, Track track) {

        if (playlist == null) {
            throw new PlaylistValidationException(
                    "Nessuna playlist selezionata",
                    "Per favore, seleziona una playlist a cui aggiungere la traccia."
            );
        }

        if (track == null) {
            throw new PlaylistValidationException(
                    "Nessuna traccia selezionata",
                    "Per favore, seleziona una traccia da aggiungere alla playlist."
            );
        }

        if (playlist.containsTrack(track)) {
            throw new PlaylistValidationException(
                    "Traccia già presente",
                    "Questa traccia è già presente nella playlist selezionata."
            );
        }

        playlist.addTrack(track);
    }

    public void removeTrackFromPlaylist(Playlist playlist, Track track) {

        if (playlist == null) {
            throw new PlaylistValidationException(
                    "Nessuna playlist selezionata",
                    "Per favore, seleziona una playlist."
            );
        }

        if (track == null) {
            throw new PlaylistValidationException(
                    "Nessuna traccia selezionata",
                    "Per favore, seleziona una traccia da rimuovere dalla playlist."
            );
        }

        boolean removed = playlist.removeTrack(track);

        if (!removed) {
            throw new PlaylistValidationException(
                    "Traccia non presente",
                    "La traccia selezionata non è presente nella playlist."
            );
        }
    }
}