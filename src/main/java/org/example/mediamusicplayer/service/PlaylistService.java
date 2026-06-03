package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

import java.util.List;

public class PlaylistService {

    public Playlist createPlaylist(String nome, MusicLibrary libreria) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new PlaylistValidationException(
                    "Nome mancante",
                    "Per favore, inserisci un nome per la nuova playlist."
            );
        }

        if (libreria == null) {
            throw new PlaylistValidationException(
                    "Libreria non disponibile",
                    "La libreria musicale non è stata inizializzata correttamente."
            );
        }

        String nomePulito = nome.trim();

        for (Playlist playlist : libreria.getPlaylists()) {
            if (playlist.getName().equalsIgnoreCase(nomePulito)) {
                throw new PlaylistValidationException(
                        "Playlist duplicata",
                        "Esiste già una playlist chiamata '" + nomePulito + "'. Scegli un nome diverso."
                );
            }
        }

        return new Playlist(nomePulito);
    }

    public void addTrackToPlaylist(Playlist playlist, Track track) {
        validatePlaylist(playlist);
        validateTrack(track, "La traccia da aggiungere non può essere nulla.");

        if (containsTrack(playlist, track)) {
            throw new PlaylistValidationException(
                    "Traccia già presente",
                    "Questa traccia è già presente nella playlist."
            );
        }

        List<Track> tracks = playlist.getTracks();
        tracks.add(track);
        playlist.setTracks(tracks);
    }

    public boolean removeTrackFromPlaylist(Playlist playlist, Track track) {
        validatePlaylist(playlist);
        validateTrack(track, "La traccia da rimuovere non può essere nulla.");

        List<Track> tracks = playlist.getTracks();
        boolean removed = tracks.remove(track);

        playlist.setTracks(tracks);

        return removed;
    }

    public boolean containsTrack(Playlist playlist, Track track) {
        validatePlaylist(playlist);

        if (track == null) {
            return false;
        }

        return playlist.getTracks().contains(track);
    }

    public int countTracks(Playlist playlist) {
        validatePlaylist(playlist);

        return playlist.getTracks().size();
    }

    public boolean isPlaylistEmpty(Playlist playlist) {
        validatePlaylist(playlist);

        return playlist.getTracks().isEmpty();
    }

    public void renamePlaylist(Playlist playlist, String nuovoNome) {
        validatePlaylist(playlist);

        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            throw new PlaylistValidationException(
                    "Nome mancante",
                    "Il nome della playlist non può essere vuoto."
            );
        }

        playlist.setName(nuovoNome.trim());
    }

    private void validatePlaylist(Playlist playlist) {
        if (playlist == null) {
            throw new PlaylistValidationException(
                    "Playlist non valida",
                    "La playlist selezionata non è valida."
            );
        }
    }

    private void validateTrack(Track track, String message) {
        if (track == null) {
            throw new PlaylistValidationException(
                    "Traccia non valida",
                    message
            );
        }
    }
}