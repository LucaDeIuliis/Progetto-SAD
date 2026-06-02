/*package org.example;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.time.Year;

class MusicLibraryTest {

    private MusicLibrary musicLibrary;

    @BeforeEach
    void setUp() {
        musicLibrary = new MusicLibrary();
    }

    @Test
    void constructorShouldInitializeEmptyLists() {
        assertNotNull(musicLibrary.getPlaylists());
        assertNotNull(musicLibrary.getAllTracks());

        assertTrue(musicLibrary.getPlaylists().isEmpty());
        assertTrue(musicLibrary.getAllTracks().isEmpty());
    }

    @Test
    void addPlaylistShouldAddPlaylist() {
        Playlist playlist = new Playlist("Rock");

        musicLibrary.addPlaylist(playlist);

        assertEquals(1, musicLibrary.getPlaylists().size());
        assertTrue(musicLibrary.getPlaylists().contains(playlist));
    }

    @Test
    void addPlaylistShouldNotAddNull() {
        musicLibrary.addPlaylist(null);

        assertTrue(musicLibrary.getPlaylists().isEmpty());
    }

    @Test
    void addPlaylistShouldNotAddDuplicates() {
        Playlist playlist = new Playlist("Rock");

        musicLibrary.addPlaylist(playlist);
        musicLibrary.addPlaylist(playlist);

        assertEquals(1, musicLibrary.getPlaylists().size());
    }

    @Test
    void addTrackToLibraryShouldAddTrack() {
        Track track = new Track(
                "Song1",
                "Artist1",
                Duration.ofSeconds(180),
                "Pop",
                Year.of(2024)
        );

        musicLibrary.addTrackToLibrary(track);

        assertEquals(1, musicLibrary.getAllTracks().size());
        assertTrue(musicLibrary.getAllTracks().contains(track));
    }

    @Test
    void addTrackToLibraryShouldNotAddNull() {
        musicLibrary.addTrackToLibrary(null);

        assertTrue(musicLibrary.getAllTracks().isEmpty());
    }

    @Test
    void addTrackToLibraryShouldNotAddDuplicates() {
        Track track = new Track(
                "Song1",
                "Artist1",
                Duration.ofSeconds(180),
                "Pop",
                Year.of(2024)
        );

        musicLibrary.addTrackToLibrary(track);
        musicLibrary.addTrackToLibrary(track);

        assertEquals(1, musicLibrary.getAllTracks().size());
    }

    @Test
    void getPlaylistsShouldReturnObservableList() {
        assertNotNull(musicLibrary.getPlaylists());
    }

    @Test
    void getAllTracksShouldReturnObservableList() {
        assertNotNull(musicLibrary.getAllTracks());
    }
}*/