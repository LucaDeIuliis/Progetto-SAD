package org.example;


import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistServiceTest {

    private PlaylistService playlistService;
    private MusicLibrary library;
    private MusicLibraryService service;

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService();
        library = new MusicLibrary();
        service = new MusicLibraryService();
    }

    @Test
    void createPlaylistShouldCreatePlaylistWithValidName() {
        Playlist playlist = playlistService.createPlaylist("Rock", library);

        assertNotNull(playlist);
        assertEquals("Rock", playlist.getName());
    }

    @Test
    void createPlaylistShouldTrimName() {
        Playlist playlist = playlistService.createPlaylist("   Rock   ", library);

        assertEquals("Rock", playlist.getName());
    }

    @Test
    void createPlaylistShouldThrowExceptionWhenNameIsNull() {
        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.createPlaylist(null, library)
        );
    }

    @Test
    void createPlaylistShouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.createPlaylist("", library)
        );
    }

    @Test
    void createPlaylistShouldThrowExceptionWhenNameContainsOnlySpaces() {
        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.createPlaylist("     ", library)
        );
    }

    @Test
    void createPlaylistShouldThrowExceptionWhenNameAlreadyExists() {
        service.addPlaylist(library, new Playlist("Rock"));

        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.createPlaylist("Rock", library)
        );
    }

    @Test
    void createPlaylistShouldThrowExceptionWhenNameAlreadyExistsIgnoringCase() {
        service.addPlaylist(library, new Playlist("Rock"));

        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.createPlaylist("rock", library)
        );
    }
}
