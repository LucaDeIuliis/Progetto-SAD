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
    public void addTrackToPlaylist(
            Playlist playlist,
            Track track
    ) {
        if (playlist == null) {
            throw new PlaylistValidationException(
                    "Playlist non disponibile",
                    "Seleziona una playlist valida."
            );
        }

        if (track == null) {
            throw new PlaylistValidationException(
                    "Traccia non disponibile",
                    "Seleziona una traccia valida."
            );
        }

        if (playlist.getTracks().contains(track)) {
            throw new PlaylistValidationException(
                    "Traccia già presente",
                    "La traccia è già presente nella playlist "
                            + playlist.getName()
                            + "."
            );
        }

        playlist.getTracks().add(track);
    }

    // =========================================================
    // LOGICA DI BUSINESS: SMART PLAYLIST AUTO-SYNC
    // =========================================================

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


    public boolean trackRispettaFiltro(Playlist playlist, Track track) {

        if (!playlist.isGenerataAutomaticamente()) {
                return true;
        }


        if (playlist.getTipoFiltro() == null ||
                    playlist.getFiltroAutomatico() == null) {
                return true;
        }


        switch (playlist.getTipoFiltro()) {

                case "Genere":
                    return track.getGenre()
                            .equalsIgnoreCase(
                                    playlist.getFiltroAutomatico()
                            );


                case "Anno":
                    return String.valueOf(
                            track.getYear().getValue()
                    ).equals(
                            playlist.getFiltroAutomatico()
                    );


                default:
                    return true;
            }
        }
    public void syncTrackWithAutomaticPlaylists(
            Track track,
            MusicLibrary library
    ) {
        for (Playlist playlist : library.getPlaylists()) {

            if (!playlist.isGenerataAutomaticamente()
                    || isSmartPlaylist(playlist)) {
                continue;
            }

            playlist.removeTrack(track);

            if (trackRispettaFiltro(playlist, track)) {
                playlist.addTrack(track);
            }
        }
    }

    public void syncSmartPlaylists(MusicLibrary library, MusicLibraryService libraryService) {
        for (TrackTag tag : TrackTag.values()) {
            String nomeBase = switch (tag) {
                case FAVOURITE -> "I Miei Preferiti";
                case EXPLICIT -> "Brani Espliciti";
                case NEW_RELEASE -> "Nuove Uscite";
            };

            String nomeCompleto =
                    nomeBase + " " + tag.getSymbol();

            java.util.List<Track> tracceTaggate =
                    library.getAllTracks()
                            .stream()
                            .filter(track ->
                                    track.getTags().contains(tag)
                            )
                            .toList();

            java.util.List<Playlist> playlistConStessoNome =
                    library.getPlaylists()
                            .stream()
                            .filter(playlist ->
                                    playlist.getName().equals(nomeCompleto)
                            )
                            .toList();

            Playlist playlistEsistente =
                    playlistConStessoNome.isEmpty()
                            ? null
                            : playlistConStessoNome.get(0);

            for (int i = 1;
                 i < playlistConStessoNome.size();
                 i++) {

                libraryService.deletePlaylist(
                        library,
                        playlistConStessoNome.get(i)
                );
            }

            if (playlistEsistente != null) {

                playlistEsistente
                        .getTracks()
                        .setAll(tracceTaggate);

                if (playlistEsistente
                        .getTracks()
                        .isEmpty()) {

                    libraryService.deletePlaylist(
                            library,
                            playlistEsistente
                    );
                }

            } else if (!tracceTaggate.isEmpty()) {

                Playlist nuovaAuto =
                        new Playlist(nomeCompleto);

                nuovaAuto.setGenerataAutomaticamente(true);
                nuovaAuto.setTipoFiltro(null);
                nuovaAuto.setFiltroAutomatico(null);

                nuovaAuto
                        .getTracks()
                        .addAll(tracceTaggate);

                libraryService.addPlaylist(
                        library,
                        nuovaAuto
                );
            }
        }
    }

    public boolean isSmartPlaylist(Playlist playlist) {
        if (playlist == null) {
            return false;
        }
        return getTagFromPlaylistName(playlist.getName()) != null;
    }

    public TrackTag getTagFromPlaylistName(String playlistName) {
        if (playlistName == null) {
            return null;
        }
        for (TrackTag tag : TrackTag.values()) {
            String nomeBase = switch (tag) {
                case FAVOURITE -> "I Miei Preferiti";
                case EXPLICIT -> "Brani Espliciti";
                case NEW_RELEASE -> "Nuove Uscite";
            };
            String nomeCompleto = nomeBase + " " + tag.getSymbol();
            if (playlistName.equals(nomeCompleto)) {
                return tag;
            }
        }
        return null;
    }

    public Playlist createAutomaticPlaylist(
            String tipoFiltro,
            String filtro,
            MusicLibrary library,
            MusicLibraryService libraryService
    ) {
        if (tipoFiltro == null
                || (!tipoFiltro.equals("Genere")
                && !tipoFiltro.equals("Anno"))) {
            throw new PlaylistValidationException(
                    "Tipo filtro non valido",
                    "Seleziona un tipo di filtro valido."
            );
        }

        if (filtro == null || filtro.trim().isEmpty()) {
            throw new PlaylistValidationException(
                    "Filtro mancante",
                    "Inserisci un genere o un anno."
            );
        }

        String filtroPulito = filtro.trim();
        String nomePlaylist =
                "Auto - " + tipoFiltro + " " + filtroPulito;

        Playlist nuovaPlaylist =
                createPlaylist(nomePlaylist, library);

        nuovaPlaylist.setGenerataAutomaticamente(true);
        nuovaPlaylist.setTipoFiltro(tipoFiltro);
        nuovaPlaylist.setFiltroAutomatico(filtroPulito);

        for (Track track : library.getAllTracks()) {
            if (trackRispettaFiltro(nuovaPlaylist, track)) {
                nuovaPlaylist.addTrack(track);
            }
        }

        if (nuovaPlaylist.getTracks().isEmpty()) {
            throw new PlaylistValidationException(
                    "Nessun risultato",
                    "Non sono state trovate tracce compatibili."
            );
        }

        libraryService.addPlaylist(
                library,
                nuovaPlaylist
        );

        return nuovaPlaylist;
    }

    public void validateTrackDataForAutomaticPlaylist(
            Playlist playlist,
            String genre,
            String year
    ) {
        if (playlist == null || !playlist.isGenerataAutomaticamente()) {
            return;
        }

        String tipoFiltro = playlist.getTipoFiltro();
        String filtro = playlist.getFiltroAutomatico();

        if (tipoFiltro == null || filtro == null) {
            return;
        }

        boolean compatibile = switch (tipoFiltro) {
            case "Genere" ->
                    genre != null
                            && genre.trim().equalsIgnoreCase(filtro);

            case "Anno" ->
                    year != null
                            && year.trim().equals(filtro);

            default -> true;
        };

        if (!compatibile) {
            throw new PlaylistValidationException(
                    "Traccia non compatibile",
                    "La traccia non rispetta il filtro della playlist automatica."
            );
        }
    }

    public void moveTrack(
            java.util.List<Track> tracks,
            int sourceIndex,
            int targetIndex
    ) {
        if (tracks == null) {
            throw new PlaylistValidationException(
                    "Lista non disponibile",
                    "Non è possibile riordinare le tracce."
            );
        }

        if (sourceIndex < 0
                || sourceIndex >= tracks.size()
                || targetIndex < 0
                || targetIndex > tracks.size()) {
            throw new PlaylistValidationException(
                    "Posizione non valida",
                    "Non è possibile spostare la traccia nella posizione richiesta."
            );
        }

        Track track = tracks.remove(sourceIndex);

        if (targetIndex > tracks.size()) {
            targetIndex = tracks.size();
        }

        tracks.add(targetIndex, track);
    }

    public void applyPlaylistTagToTrack(
            Playlist playlist,
            Track track
    ) {
        if (playlist == null || track == null) {
            return;
        }

        if (!isSmartPlaylist(playlist)) {
            return;
        }

        TrackTag tag = getTagFromPlaylistName(
                playlist.getName()
        );

        if (tag != null) {
            track.addTag(tag);
        }
    }

    public TrackTag getTagFromPlaylist(Playlist playlist) {
        if (playlist == null) {
            return null;
        }

        return getTagFromPlaylistName(
                playlist.getName()
        );
    }
}