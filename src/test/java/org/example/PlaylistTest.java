package org.example;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
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
                Duration.ofSeconds(355),
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
    }

    @Test
    void getTracksShouldReturnTrackList() {
        playlist.addTrack(track);

        assertNotNull(playlist.getTracks());
        assertEquals(1, playlist.getTracks().size());
    }

    @Test
    void toStringShouldReturnPlaylistName() {
        assertEquals("Rock", playlist.toString());
    }

    @Test
    void addMultipleTracksShouldIncreaseSize() {
        Track track2 = new Track(
                "Imagine",
                "John Lennon",
                Duration.ofSeconds(183),
                "Pop",
                Year.of(1971)
        );

        playlist.addTrack(track);
        playlist.addTrack(track2);

        assertEquals(2, playlist.getTracks().size());
    }

    @Test
    void removeTrackNotPresentShouldNotThrowException() {
        assertDoesNotThrow(() -> playlist.removeTrack(track));
        assertTrue(playlist.getTracks().isEmpty());
    }
}
