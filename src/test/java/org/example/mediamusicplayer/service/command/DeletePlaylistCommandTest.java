package org.example.mediamusicplayer.service.command;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeletePlaylistCommandTest {

    private MusicLibrary library;
    private MusicLibraryService libraryService;

    private Playlist playlist1;
    private Playlist playlist2;
    private Playlist playlist3;


    @BeforeEach
    void setUp() {

        library = new MusicLibrary();
        libraryService = new MusicLibraryService();

        playlist1 = new Playlist("Playlist 1");
        playlist2 = new Playlist("Playlist 2");
        playlist3 = new Playlist("Playlist 3");


        library.getPlaylists().add(playlist1);
        library.getPlaylists().add(playlist2);
        library.getPlaylists().add(playlist3);
    }


    @Test
    void execute_ShouldRemovePlaylistFromLibrary() {

        DeletePlaylistCommand command =
                new DeletePlaylistCommand(
                        playlist2,
                        library,
                        libraryService
                );


        command.execute();


        assertFalse(
                library.getPlaylists().contains(playlist2)
        );

        assertEquals(
                2,
                library.getPlaylists().size()
        );
    }


    @Test
    void undo_ShouldRestoreDeletedPlaylist() {

        DeletePlaylistCommand command =
                new DeletePlaylistCommand(
                        playlist2,
                        library,
                        libraryService
                );


        command.execute();

        command.undo();


        assertTrue(
                library.getPlaylists().contains(playlist2)
        );

        assertEquals(
                3,
                library.getPlaylists().size()
        );
    }


    @Test
    void undo_ShouldRestoreOriginalPosition() {

        DeletePlaylistCommand command =
                new DeletePlaylistCommand(
                        playlist2,
                        library,
                        libraryService
                );


        int originalIndex =
                library.getPlaylists().indexOf(playlist2);


        command.execute();
        command.undo();


        assertEquals(
                originalIndex,
                library.getPlaylists().indexOf(playlist2)
        );
    }
}
