package org.example.mediamusicplayer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    private Playlist playlist;
    private Track track;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("Rock");

        track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(354),
                "Rock",
                Year.of(1975)
        );
    }

    @Test
    void constructorShouldInitializeNameAndEmptyTrackList() {
        assertEquals("Rock", playlist.getName());
        assertNotNull(playlist.getTracks());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    void setNameShouldUpdatePlaylistName() {
        playlist.setName("Pop");

        assertEquals("Pop", playlist.getName());
    }

    @Test
    void addTrackShouldAddTrackToPlaylist() {
        playlist.addTrack(track);

        assertEquals(1, playlist.getTracks().size());
        assertTrue(playlist.getTracks().contains(track));
    }

    @Test
    void removeTrackShouldRemoveTrackFromPlaylist() {
        playlist.addTrack(track);

        playlist.removeTrack(track);

        assertTrue(playlist.getTracks().isEmpty());
        assertFalse(playlist.getTracks().contains(track));
    }

    @Test
    void getTracksShouldReturnTrackList() {
        playlist.addTrack(track);

        assertEquals(1, playlist.getTracks().size());
        assertEquals(track, playlist.getTracks().get(0));
    }

    @Test
    void toStringShouldReturnPlaylistName() {
        assertEquals("Rock", playlist.toString());
    }
}