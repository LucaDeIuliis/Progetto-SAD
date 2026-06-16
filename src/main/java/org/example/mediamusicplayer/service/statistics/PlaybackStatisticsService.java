package org.example.mediamusicplayer.service.statistics;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;

import java.util.Comparator;
import java.util.List;

public class PlaybackStatisticsService {

    /*
     * Registra una riproduzione completata di una traccia.
     */
    public void registerTrackPlayback(Track track) {
        if (track == null) {
            return;
        }

        track.incrementPlayCount();
    }

    /*
     * Registra l'avvio di una playlist.
     */
    public void registerPlaylistPlayback(Playlist playlist) {
        if (playlist == null) {
            return;
        }

        playlist.incrementPlayCount();
    }

    /*
     * Restituisce le tracce più riprodotte, ordinate in modo decrescente.
     * Le tracce mai riprodotte vengono escluse.
     */
    public List<Track> getMostPlayedTracks(MusicLibrary library, int limit) {
        if (library == null || limit <= 0) {
            return List.of();
        }

        return library.getAllTracks().stream()
                .filter(track -> track.getPlayCount() > 0)
                .sorted(
                        Comparator.comparingInt(Track::getPlayCount)
                                .reversed()
                                .thenComparing(Track::getTitle)
                )
                .limit(limit)
                .toList();
    }

    /*
     * Restituisce le playlist più riprodotte, ordinate in modo decrescente.
     * Le playlist mai riprodotte vengono escluse.
     */
    public List<Playlist> getMostPlayedPlaylists(MusicLibrary library, int limit) {
        if (library == null || limit <= 0) {
            return List.of();
        }

        return library.getPlaylists().stream()
                .filter(playlist -> playlist.getPlayCount() > 0)
                .sorted(
                        Comparator.comparingInt(Playlist::getPlayCount)
                                .reversed()
                                .thenComparing(Playlist::getName)
                )
                .limit(limit)
                .toList();
    }
}