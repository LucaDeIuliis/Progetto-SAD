package org.example.mediamusicplayer.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    @Test
    void playlistShouldBeCreatedWithNameAndEmptyTrackList() {
        Playlist playlist = new Playlist("Rock");

        assertNotNull(playlist.getId());
        assertEquals("Rock", playlist.getName());
        assertTrue(playlist.isEmpty());
        assertEquals(0, playlist.getNumberOfTracks());
    }

    @Test
    void setNameShouldUpdatePlaylistName() {
        Playlist playlist = new Playlist("Rock");

        playlist.setName("Pop");

        assertEquals("Pop", playlist.getName());
    }

    @Test
    void addTrackShouldIncreaseNumberOfTracks() {
        Playlist playlist = new Playlist("Rock");
        Track track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(355),
                "Rock",
                Year.of(1975)
        );

        playlist.addTrack(track);

        assertFalse(playlist.isEmpty());
        assertEquals(1, playlist.getNumberOfTracks());
        assertTrue(playlist.containsTrack(track));
    }

    @Test
    void removeTrackShouldRemoveExistingTrack() {
        Playlist playlist = new Playlist("Rock");
        Track track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(355),
                "Rock",
                Year.of(1975)
        );

        playlist.addTrack(track);
        boolean removed = playlist.removeTrack(track);

        assertTrue(removed);
        assertTrue(playlist.isEmpty());
        assertEquals(0, playlist.getNumberOfTracks());
        assertFalse(playlist.containsTrack(track));
    }

    @Test
    void removeTrackShouldReturnFalseWhenTrackIsNotPresent() {
        Playlist playlist = new Playlist("Rock");
        Track track = new Track(
                "Imagine",
                "John Lennon",
                Duration.ofSeconds(183),
                "Pop",
                Year.of(1971)
        );

        boolean removed = playlist.removeTrack(track);

        assertFalse(removed);
        assertEquals(0, playlist.getNumberOfTracks());
    }

    @Test
    void getTracksShouldReturnCopyOfTrackList() {
        Playlist playlist = new Playlist("Rock");
        Track track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(355),
                "Rock",
                Year.of(1975)
        );

        playlist.addTrack(track);

        List<Track> externalList = playlist.getTracks();
        externalList.clear();

        assertEquals(1, playlist.getNumberOfTracks());
        assertTrue(playlist.containsTrack(track));
    }

    @Test
    void fullConstructorShouldCreatePlaylistWithExistingTracks() {
        Track track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(355),
                "Rock",
                Year.of(1975)
        );

        Playlist playlist = new Playlist(
                "playlist-1",
                "Rock Classics",
                List.of(track)
        );

        assertEquals("playlist-1", playlist.getId());
        assertEquals("Rock Classics", playlist.getName());
        assertEquals(1, playlist.getNumberOfTracks());
        assertTrue(playlist.containsTrack(track));
    }

    @Test
    void toStringShouldReturnPlaylistName() {
        Playlist playlist = new Playlist("Rock");

        assertEquals("Rock", playlist.toString());
    }
}