package org.example.mediamusicplayer.model;


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
    void constructorShouldInitializeEmptyCollections() {
        assertNotNull(musicLibrary.getPlaylists());
        assertNotNull(musicLibrary.getAllTracks());

        assertTrue(musicLibrary.getPlaylists().isEmpty());
        assertTrue(musicLibrary.getAllTracks().isEmpty());
    }

    @Test
    void getPlaylistsShouldReturnPlaylistCollection() {
        Playlist playlist = new Playlist("Rock");

        musicLibrary.getPlaylists().add(playlist);

        assertEquals(1, musicLibrary.getPlaylists().size());
        assertTrue(musicLibrary.getPlaylists().contains(playlist));
    }

    @Test
    void getAllTracksShouldReturnTrackCollection() {
        Track track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(354),
                "Rock",
                Year.of(1975)
        );

        musicLibrary.getAllTracks().add(track);

        assertEquals(1, musicLibrary.getAllTracks().size());
        assertTrue(musicLibrary.getAllTracks().contains(track));
    }

    @Test
    void collectionsShouldAllowMultipleElements() {
        Playlist p1 = new Playlist("Rock");
        Playlist p2 = new Playlist("Pop");

        Track t1 = new Track(
                "Song 1",
                "Artist 1",
                Duration.ofSeconds(180),
                "Rock",
                Year.of(2020)
        );

        Track t2 = new Track(
                "Song 2",
                "Artist 2",
                Duration.ofSeconds(240),
                "Pop",
                Year.of(2021)
        );

        musicLibrary.getPlaylists().addAll(p1, p2);
        musicLibrary.getAllTracks().addAll(t1, t2);

        assertEquals(2, musicLibrary.getPlaylists().size());
        assertEquals(2, musicLibrary.getAllTracks().size());
    }
}