package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShufflePlaybackStrategyTest {

    private Track track(String name) {
        return new Track(name, "Artist",
                Duration.ofSeconds(100),
                "Rock",
                Year.of(2020));
    }

    @Test
    void getNextTrack_SingleTrack_ShouldReturnSameTrack() {

        Track t1 = track("A");

        ShufflePlaybackStrategy strategy =
                new ShufflePlaybackStrategy();

        assertEquals(
                t1,
                strategy.getNextTrack(List.of(t1), t1)
        );
    }

    @Test
    void getNextTrack_ShouldReturnDifferentTrack() {

        Track t1 = track("A");
        Track t2 = track("B");

        ShufflePlaybackStrategy strategy =
                new ShufflePlaybackStrategy();

        Track result =
                strategy.getNextTrack(List.of(t1, t2), t1);

        assertNotNull(result);
        assertNotEquals(t1, result);
    }

    @Test
    void getNextTrack_NullPlaylist_ShouldReturnNull() {

        ShufflePlaybackStrategy strategy =
                new ShufflePlaybackStrategy();

        assertNull(
                strategy.getNextTrack(null, null)
        );
    }
}