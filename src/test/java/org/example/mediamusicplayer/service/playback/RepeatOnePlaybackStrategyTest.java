package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class RepeatOnePlaybackStrategyTest {

    @Test
    void getNextTrack_ShouldAlwaysReturnCurrentTrack() {

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(100),
                "Rock",
                Year.of(2020)
        );

        RepeatOnePlaybackStrategy strategy =
                new RepeatOnePlaybackStrategy();

        assertEquals(
                track,
                strategy.getNextTrack(null, track)
        );
    }
}