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
    @Test
    void setName_ShouldUpdateName() {

        Playlist playlist = new Playlist("Rock");

        playlist.setName("Metal");

        assertEquals("Metal", playlist.getName());
    }

    @Test
    void addMultipleTracks_ShouldStoreAllTracks() {

        Playlist playlist = new Playlist("Rock");

        Track t1 = new Track(
                "Song1",
                "Artist1",
                Duration.ofSeconds(100),
                "Rock",
                Year.of(2020)
        );

        Track t2 = new Track(
                "Song2",
                "Artist2",
                Duration.ofSeconds(200),
                "Rock",
                Year.of(2021)
        );

        playlist.addTrack(t1);
        playlist.addTrack(t2);

        assertEquals(2, playlist.getTracks().size());
    }
}