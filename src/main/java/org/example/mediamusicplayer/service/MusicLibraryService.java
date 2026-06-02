package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

public class MusicLibraryService {

    // Aggiunge una playlist alla libreria con controlli
    public void addPlaylist(MusicLibrary libreria, Playlist playlist) {
        if (playlist == null) {
            throw new PlaylistValidationException("Errore di Sistema", "Impossibile aggiungere una playlist nulla.");
        }

        // Verifica duplicati a livello globale
        if (libreria.getPlaylists().contains(playlist)) {
            throw new PlaylistValidationException("Playlist Duplicata", "Questa playlist è già presente nella libreria.");
        }

        libreria.getPlaylists().add(playlist);
    }

    // Aggiunge una traccia al magazzino globale con controlli
    public void addTrackToLibrary(MusicLibrary libreria, Track track) {
        if (track == null) {
            throw new TrackValidationException("Errore di Sistema", "Impossibile aggiungere una traccia nulla.");
        }

        // Evitiamo che la stessa identica traccia venga inserita due volte nel magazzino globale
        if (!libreria.getAllTracks().contains(track)) {
            libreria.getAllTracks().add(track);
        }
    }

    // Rimuove in modo sicuro una traccia sia dal magazzino che da tutte le playlist
    public void deleteTrackGlobal(MusicLibrary libreria, Track track) {
        if (track != null) {
            libreria.getAllTracks().remove(track);
            // Eliminazione a cascata
            for (Playlist p : libreria.getPlaylists()) {
                p.removeTrack(track);
            }
        }
    }
}