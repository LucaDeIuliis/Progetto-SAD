package org.example.mediamusicplayer.service.command;


import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.PlaylistService;

public class AssignTrackCommand implements Command {

    private final Playlist playlist;
    private final Track traccia;
    private final PlaylistService playlistService;
    private boolean successfullyAdded = false;

    public AssignTrackCommand(Playlist playlist, Track traccia, PlaylistService playlistService) {
        this.playlist = playlist;
        this.traccia = traccia;
        this.playlistService = playlistService;
    }

    @Override
    public void execute() {
        try {
            playlistService.addTrackToPlaylist(playlist, traccia);
            successfullyAdded = true;
        } catch (PlaylistValidationException e) {
            // Il Controller gestirà l'eccezione prima, qui non dovrebbe mai arrivare,
            // ma lo gestiamo per sicurezza.
            successfullyAdded = false;
        }
    }

    @Override
    public void undo() {
        if (successfullyAdded) {
            playlist.getTracks().remove(traccia);
        }
    }
}
