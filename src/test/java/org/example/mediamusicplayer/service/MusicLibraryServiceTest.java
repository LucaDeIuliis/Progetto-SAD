package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class MusicLibraryServiceTest {

    private MusicLibraryService service;
    private MusicLibrary library;

    @BeforeEach
    void setUp() {
        service = new MusicLibraryService();
        library = new MusicLibrary();
    }

    private Track createTrack() {
        return new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(180),
                "Rock",
                Year.of(2020)
        );
    }

    @Test
    void addPlaylist_ShouldAddPlaylist() {

        Playlist playlist = new Playlist("Rock");

        service.addPlaylist(library, playlist);

        assertEquals(1, library.getPlaylists().size());
        assertTrue(library.getPlaylists().contains(playlist));
    }

    @Test
    void addPlaylist_NullPlaylist_ShouldThrowException() {

        assertThrows(
                PlaylistValidationException.class,
                () -> service.addPlaylist(library, null)
        );
    }

    @Test
    void addPlaylist_DuplicatePlaylist_ShouldThrowException() {

        Playlist playlist = new Playlist("Rock");

        library.getPlaylists().add(playlist);

        assertThrows(
                PlaylistValidationException.class,
                () -> service.addPlaylist(library, playlist)
        );
    }

    @Test
    void addTrackToLibrary_ShouldAddTrack() {

        Track track = createTrack();

        service.addTrackToLibrary(library, track);

        assertEquals(1, library.getAllTracks().size());
        assertTrue(library.getAllTracks().contains(track));
    }

    @Test
    void addTrackToLibrary_NullTrack_ShouldThrowException() {

        assertThrows(
                TrackValidationException.class,
                () -> service.addTrackToLibrary(library, null)
        );
    }

    @Test
    void addTrackToLibrary_SameTrackTwice_ShouldNotDuplicate() {

        Track track = createTrack();

        service.addTrackToLibrary(library, track);
        service.addTrackToLibrary(library, track);

        assertEquals(1, library.getAllTracks().size());
    }

    @Test
    void deletePlaylist_ShouldRemovePlaylist() {

        Playlist playlist = new Playlist("Rock");

        library.getPlaylists().add(playlist);

        service.deletePlaylist(library, playlist);

        assertFalse(library.getPlaylists().contains(playlist));
    }

    @Test
    void deletePlaylist_NullPlaylist_ShouldDoNothing() {

        assertDoesNotThrow(
                () -> service.deletePlaylist(library, null)
        );
    }

    @Test
    void deleteTrackGlobal_ShouldRemoveTrackEverywhere() {

        Track track = createTrack();

        Playlist p1 = new Playlist("P1");
        Playlist p2 = new Playlist("P2");

        p1.addTrack(track);
        p2.addTrack(track);

        library.getPlaylists().addAll(p1, p2);
        library.getAllTracks().add(track);

        service.deleteTrackGlobal(library, track);

        assertFalse(library.getAllTracks().contains(track));
        assertFalse(p1.getTracks().contains(track));
        assertFalse(p2.getTracks().contains(track));
    }

    @Test
    void deleteTrackGlobal_NullTrack_ShouldDoNothing() {

        assertDoesNotThrow(
                () -> service.deleteTrackGlobal(library, null)
        );
    }

    @Test
    void addPlaylist_DuplicatePlaylist_ShouldNotModifyLibrary() {

        Playlist playlist = new Playlist("Rock");

        library.getPlaylists().add(playlist);

        assertThrows(
                PlaylistValidationException.class,
                () -> service.addPlaylist(library, playlist)
        );

        assertEquals(1, library.getPlaylists().size());
    }

    @Test
    void deletePlaylist_NotExistingPlaylist_ShouldDoNothing() {

        Playlist playlist = new Playlist("Rock");

        assertDoesNotThrow(
                () -> service.deletePlaylist(library, playlist)
        );

        assertTrue(library.getPlaylists().isEmpty());
    }

    @Test
    void deleteTrackGlobal_ShouldNotRemoveOtherTracks() {

        Track track1 = createTrack();

        Track track2 = new Track(
                "Song2",
                "Artist2",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2021)
        );

        Playlist playlist = new Playlist("Mix");

        playlist.addTrack(track1);
        playlist.addTrack(track2);

        library.getPlaylists().add(playlist);
        library.getAllTracks().add(track1);
        library.getAllTracks().add(track2);

        service.deleteTrackGlobal(library, track1);

        assertFalse(library.getAllTracks().contains(track1));
        assertTrue(library.getAllTracks().contains(track2));

        assertFalse(playlist.getTracks().contains(track1));
        assertTrue(playlist.getTracks().contains(track2));
    }

    @Test
    void deleteTrackGlobal_NotExistingTrack_ShouldDoNothing() {

        Track track = createTrack();

        assertDoesNotThrow(
                () -> service.deleteTrackGlobal(library, track)
        );

        assertTrue(library.getAllTracks().isEmpty());
    }
}