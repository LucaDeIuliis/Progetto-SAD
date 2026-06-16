package org.example.mediamusicplayer.service.command;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.model.TrackTag;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.service.PlaylistService;

import java.util.HashMap;
import java.util.Map;

public class DeleteTrackCommand implements Command {

    private final Track traccia;
    private final Playlist playlistAttuale;
    private final MusicLibrary libreria;
    private final MusicLibraryService libraryService;
    private final PlaylistService playlistService;
    private final TrackTag potenzialeTag;

    // Fotografia dello stato globale
    private final int globalIndex;
    private final Map<Playlist, Integer> posizioniNellePlaylist = new HashMap<>();

    // Fotografia dello stato locale
    private final int localIndex;

    public DeleteTrackCommand(Track traccia, Playlist playlistAttuale, TrackTag potenzialeTag, MusicLibrary libreria, MusicLibraryService libraryService, PlaylistService playlistService) {
        this.traccia = traccia;
        this.playlistAttuale = playlistAttuale;
        this.potenzialeTag = potenzialeTag;
        this.libreria = libreria;
        this.libraryService = libraryService;
        this.playlistService = playlistService;

        if (playlistAttuale == null) {
            // Eliminazione globale
            this.globalIndex = libreria.getAllTracks().indexOf(traccia);
            for (Playlist p : libreria.getPlaylists()) {
                if (p.getTracks().contains(traccia)) {
                    posizioniNellePlaylist.put(p, p.getTracks().indexOf(traccia));
                }
            }
            this.localIndex = -1;
        } else {
            // Eliminazione locale (da playlist normale)
            this.globalIndex = -1;
            this.localIndex = (potenzialeTag == null) ? playlistAttuale.getTracks().indexOf(traccia) : -1;
        }
    }

    @Override
    public void execute() {
        if (playlistAttuale == null) {
            libraryService.deleteTrackGlobal(libreria, traccia);
        } else {
            if (potenzialeTag != null) {
                traccia.getTags().remove(potenzialeTag);
            } else {
                playlistService.removeTrackFromPlaylist(playlistAttuale, traccia);
            }
        }
    }

    @Override
    public void undo() {
        if (playlistAttuale == null) {
            libreria.getAllTracks().add(globalIndex, traccia);
            for (Map.Entry<Playlist, Integer> entry : posizioniNellePlaylist.entrySet()) {
                entry.getKey().getTracks().add(entry.getValue(), traccia);
            }
        } else {
            if (potenzialeTag != null) {
                traccia.addTag(potenzialeTag);
            } else {
                playlistAttuale.getTracks().add(localIndex, traccia);
            }
        }
    }
}