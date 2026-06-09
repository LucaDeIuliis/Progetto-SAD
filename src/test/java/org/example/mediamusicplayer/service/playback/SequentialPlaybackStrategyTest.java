package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SequentialPlaybackStrategyTest {

    private Track track(String name) {
        return new Track(name, "Artist",
                Duration.ofSeconds(100),
                "Rock",
                Year.of(2020));
    }

    @Test
    void getNextTrack_ShouldReturnNextTrack() {

        Track t1 = track("A");
        Track t2 = track("B");
        Track t3 = track("C");

        SequentialPlaybackStrategy strategy =
                new SequentialPlaybackStrategy();

        assertEquals(
                t2,
                strategy.getNextTrack(List.of(t1, t2, t3), t1)
        );
    }

    @Test
    void getNextTrack_LastTrack_ShouldReturnNull() {

        Track t1 = track("A");
        Track t2 = track("B");

        SequentialPlaybackStrategy strategy =
                new SequentialPlaybackStrategy();

        assertNull(
                strategy.getNextTrack(List.of(t1, t2), t2)
        );
    }

    @Test
    void getNextTrack_NullPlaylist_ShouldReturnNull() {

        SequentialPlaybackStrategy strategy =
                new SequentialPlaybackStrategy();

        assertNull(
                strategy.getNextTrack(null, null)
        );
    }

    @Test
    void getNextTrack_TrackNotPresent_ShouldReturnNull() {

        Track t1 = track("A");
        Track t2 = track("B");

        SequentialPlaybackStrategy strategy =
                new SequentialPlaybackStrategy();

        assertNull(
                strategy.getNextTrack(List.of(t1), t2)
        );
    }
}