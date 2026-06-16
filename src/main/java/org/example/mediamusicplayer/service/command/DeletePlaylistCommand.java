package org.example.mediamusicplayer.service.command;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.service.MusicLibraryService;

public class DeletePlaylistCommand implements Command {

    private final Playlist playlistDaEliminare;
    private final MusicLibrary libreria;
    private final MusicLibraryService libraryService;
    private final int indiceOriginale;

    // Costruttore: "Fotografa" lo stato prima di agire
    public DeletePlaylistCommand(Playlist playlistDaEliminare, MusicLibrary libreria, MusicLibraryService libraryService) {
        this.playlistDaEliminare = playlistDaEliminare;
        this.libreria = libreria;
        this.libraryService = libraryService;
        // Ci salviamo la posizione esatta della playlist!
        this.indiceOriginale = libreria.getPlaylists().indexOf(playlistDaEliminare);
    }

    @Override
    public void execute() {
        // AZIONE NORMALE
        libraryService.deletePlaylist(libreria, playlistDaEliminare);
    }

    @Override
    public void undo() {
        // ANNULLAMENTO: Rimettiamo la playlist esattamente dove stava
        libreria.getPlaylists().add(indiceOriginale, playlistDaEliminare);
    }
}