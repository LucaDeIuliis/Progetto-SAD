package org.example.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class MusicLibraryServiceTest {

    private MusicLibraryService service;
    private MusicLibrary library;
    private Playlist playlist;
    private Track track;

    @BeforeEach
    void setUp() {
        service = new MusicLibraryService();
        library = new MusicLibrary();

        playlist = new Playlist("Rock");

        track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(355),
                "Rock",
                Year.of(1975)
        );
    }

    @Test
    void addPlaylistShouldAddPlaylist() {
        service.addPlaylist(library, playlist);

        assertEquals(1, library.getPlaylists().size());
        assertTrue(library.getPlaylists().contains(playlist));
    }

    @Test
    void addPlaylistShouldThrowExceptionWhenPlaylistIsNull() {
        assertThrows(
                PlaylistValidationException.class,
                () -> service.addPlaylist(library, null)
        );
    }

    @Test
    void addPlaylistShouldThrowExceptionWhenPlaylistAlreadyExists() {
        service.addPlaylist(library, playlist);

        assertThrows(
                PlaylistValidationException.class,
                () -> service.addPlaylist(library, playlist)
        );
    }

    @Test
    void addTrackToLibraryShouldAddTrack() {
        service.addTrackToLibrary(library, track);

        assertEquals(1, library.getAllTracks().size());
        assertTrue(library.getAllTracks().contains(track));
    }

    @Test
    void addTrackToLibraryShouldThrowExceptionWhenTrackIsNull() {
        assertThrows(
                TrackValidationException.class,
                () -> service.addTrackToLibrary(library, null)
        );
    }

    @Test
    void addTrackToLibraryShouldNotAddDuplicateTrack() {
        service.addTrackToLibrary(library, track);
        service.addTrackToLibrary(library, track);

        assertEquals(1, library.getAllTracks().size());
    }

    @Test
    void deletePlaylistShouldRemovePlaylist() {
        service.addPlaylist(library, playlist);

        service.deletePlaylist(library, playlist);

        assertTrue(library.getPlaylists().isEmpty());
    }

    @Test
    void deletePlaylistWithNullShouldDoNothing() {
        assertDoesNotThrow(() ->
                service.deletePlaylist(library, null)
        );

        assertTrue(library.getPlaylists().isEmpty());
    }

    @Test
    void deleteTrackGlobalShouldRemoveTrackFromLibrary() {
        service.addTrackToLibrary(library, track);

        service.deleteTrackGlobal(library, track);

        assertFalse(library.getAllTracks().contains(track));
    }

    @Test
    void deleteTrackGlobalShouldRemoveTrackFromAllPlaylists() {
        Playlist playlist2 = new Playlist("Pop");

        service.addPlaylist(library, playlist);
        service.addPlaylist(library, playlist2);

        playlist.addTrack(track);
        playlist2.addTrack(track);

        service.addTrackToLibrary(library, track);

        service.deleteTrackGlobal(library, track);

        assertFalse(playlist.getTracks().contains(track));
        assertFalse(playlist2.getTracks().contains(track));
        assertFalse(library.getAllTracks().contains(track));
    }

    @Test
    void deleteTrackGlobalWithNullShouldDoNothing() {
        assertDoesNotThrow(() ->
                service.deleteTrackGlobal(library, null)
        );
    }
}