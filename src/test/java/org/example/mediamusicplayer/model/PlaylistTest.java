package org.example.mediamusicplayer.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    @Test
    void constructor_ShouldSetName() {

        Playlist playlist = new Playlist("Rock");

        assertEquals("Rock", playlist.getName());
        assertNotNull(playlist.getTracks());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    void addTrack_ShouldAddTrack() {

        Playlist playlist = new Playlist("Rock");

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2020)
        );

        playlist.addTrack(track);

        assertTrue(playlist.getTracks().contains(track));
    }

    @Test
    void removeTrack_ShouldRemoveTrack() {

        Playlist playlist = new Playlist("Rock");

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2020)
        );

        playlist.addTrack(track);
        playlist.removeTrack(track);

        assertFalse(playlist.getTracks().contains(track));
    }

    @Test
    void toString_ShouldReturnName() {

        Playlist playlist = new Playlist("Rock");

        assertEquals("Rock", playlist.toString());
    }
}