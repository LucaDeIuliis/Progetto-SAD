package org.example.mediamusicplayer.service;

import javafx.application.Platform;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.playback.PlaybackObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class AudioPlayerServiceTest {

    private AudioPlayerService service;

    private Track track1;
    private Track track2;

    @BeforeAll
    static void initJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.startup(latch::countDown);

        latch.await();
    }

    @BeforeEach
    void setUp() {
        service = new AudioPlayerService();

        track1 = new Track(
                "Song 1",
                "Author 1",
                Duration.ofSeconds(10),
                "Rock",
                Year.of(2020)
        );

        track2 = new Track(
                "Song 2",
                "Author 2",
                Duration.ofSeconds(20),
                "Pop",
                Year.of(2021)
        );
    }

    @Test
    void shouldStartPlayingTrack() {

        service.playTrack(track1, List.of(track1, track2));

        assertEquals(track1, service.getCurrentTrack());
        assertTrue(service.isPlaying());
        assertEquals("0:00", service.getFormattedCurrentTime());
    }

    @Test
    void shouldPausePlayback() {

        service.playTrack(track1, List.of(track1, track2));

        service.pause();

        assertTrue(service.isPaused());
    }

    @Test
    void shouldResumePlayback() {

        service.playTrack(track1, List.of(track1, track2));

        service.pause();
        service.resume();

        assertTrue(service.isPlaying());
    }

    @Test
    void shouldStopPlayback() {

        service.playTrack(track1, List.of(track1, track2));

        service.stop();

        assertNull(service.getCurrentTrack());
        assertEquals("0:00", service.getFormattedCurrentTime());
        assertFalse(service.isPlaying());
    }

    @Test
    void shouldPlayNextTrackSequentially() {

        service.playTrack(track1, List.of(track1, track2));

        service.playNextTrack();

        assertEquals(track2, service.getCurrentTrack());
    }
}