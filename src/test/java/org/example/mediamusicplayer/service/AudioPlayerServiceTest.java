/*package org.example.mediamusicplayer.service;

import javafx.application.Platform;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.playback.SequentialPlaybackStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class AudioPlayerServiceTest {

    private AudioPlayerService service;

    @BeforeAll
    static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // JavaFX già inizializzato
        }
    }

    @BeforeEach
    void setup() {
        service = new AudioPlayerService();
    }

    private Track createTrack(String title) {
        return new Track(
                title,
                "Artist",
                Duration.ofSeconds(180),
                "Rock",
                Year.of(2020)
        );
    }

    @Test
    void constructor_ShouldInitializeSequentialStrategy() {

        assertNull(service.getCurrentTrack());
        assertEquals("0:00", service.getFormattedCurrentTime());
        assertFalse(service.isPlaying());
        assertFalse(service.isPaused());
    }

    @Test
    void playTrack_ShouldSetCurrentTrack() {

        Track track = createTrack("Song");

        service.playTrack(track);

        assertEquals(track, service.getCurrentTrack());
    }

    @Test
    void playTrack_ShouldInvokeTrackChangedCallback() {

        AtomicBoolean called = new AtomicBoolean(false);

        service.setOnTrackChanged(() -> called.set(true));

        service.playTrack(createTrack("Song"));

        assertTrue(called.get());
    }

    @Test
    void playTrack_ShouldInvokeTimeUpdateCallback() {

        AtomicBoolean called = new AtomicBoolean(false);

        service.setOnTimeUpdate(() -> called.set(true));

        service.playTrack(createTrack("Song"));

        assertTrue(called.get());
    }

    @Test
    void stop_ShouldResetTime() {

        service.playTrack(createTrack("Song"));

        service.stop();

        assertEquals("0:00", service.getFormattedCurrentTime());
    }

    @Test
    void stop_ShouldClearCurrentTrack() {

        Track track = createTrack("Song");

        service.playTrack(track);

        service.stop();

        assertNull(service.getCurrentTrack());
    }

    @Test
    void playNextTrack_ShouldMoveToNextTrack() {

        Track first = createTrack("Track1");
        Track second = createTrack("Track2");

        service.setPlaybackStrategy(new SequentialPlaybackStrategy());

        service.playTrack(first, List.of(first, second));

        service.playNextTrack();

        assertEquals(second, service.getCurrentTrack());
    }

    @Test
    void playNextTrack_LastTrack_ShouldFinishPlayback() {

        Track track = createTrack("Track1");

        AtomicBoolean finished = new AtomicBoolean(false);

        service.setOnTrackFinished(() -> finished.set(true));

        service.playTrack(track, List.of(track));

        service.playNextTrack();

        assertNull(service.getCurrentTrack());
        assertTrue(finished.get());
    }

    @Test
    void playNextTrack_WithNullPlaylist_ShouldFinishPlayback() {

        Track track = createTrack("Track1");

        service.playTrack(track);

        service.playNextTrack();

        assertNull(service.getCurrentTrack());
    }

    @Test
    void setPlaybackStrategy_Null_ShouldKeepCurrentStrategy() {

        service.setPlaybackStrategy(null);

        assertDoesNotThrow(() ->
                service.playTrack(createTrack("Song"))
        );
    }

    @Test
    void playTrack_NullTrack_ShouldNotThrowException() {

        assertDoesNotThrow(() ->
                service.playTrack(null)
        );

        assertNull(service.getCurrentTrack());
    }

    @Test
    void stop_ShouldInvokeTimeUpdateCallback() {

        AtomicBoolean called = new AtomicBoolean(false);

        service.setOnTimeUpdate(() -> called.set(true));

        service.stop();

        assertTrue(called.get());
    }

    @Test
    void playNextTrack_ShouldInvokeTrackChangedCallback() {

        Track first = createTrack("Track1");
        Track second = createTrack("Track2");

        AtomicBoolean called = new AtomicBoolean(false);

        service.setOnTrackChanged(() -> called.set(true));

        service.playTrack(first, List.of(first, second));

        called.set(false);

        service.playNextTrack();

        assertTrue(called.get());
    }
    @Test
    void updateCurrentPlaylistQueue_ShouldUseNewTracksForSequentialPlayback() {
        Track first = createTrack("A");
        Track second = createTrack("B");

        service.playTrack(first, java.util.List.of(first));
        service.updateCurrentPlaylistQueue(java.util.List.of(first, second));

        service.playNextTrack();

        assertEquals(second, service.getCurrentTrack());
    }
    @Test
    void updateCurrentPlaylistQueue_WithMultipleTracks_ShouldPlayAllSequentially() {

        Track first = createTrack("A");
        Track second = createTrack("B");
        Track third = createTrack("C");

        service.playTrack(first, List.of(first));

        service.updateCurrentPlaylistQueue(
                List.of(first, second, third)
        );

        service.playNextTrack();
        assertEquals(second, service.getCurrentTrack());

        service.playNextTrack();
        assertEquals(third, service.getCurrentTrack());
    }

    @Test
    void updateCurrentPlaylistQueue_WhilePaused_ShouldPreserveCurrentTrack() {

        Track first = createTrack("A");
        Track second = createTrack("B");

        service.playTrack(first, List.of(first, second));

        service.pause();

        service.updateCurrentPlaylistQueue(
                List.of(first, second, createTrack("C"))
        );

        assertEquals(first, service.getCurrentTrack());
        assertTrue(service.isPaused());
    }

    @Test
    void updateCurrentPlaylistQueue_OnPenultimateTrack_ShouldReachNewTrack() {

        Track first = createTrack("A");
        Track second = createTrack("B");
        Track third = createTrack("C");

        service.playTrack(first, List.of(first, second));

        service.playNextTrack();

        assertEquals(second, service.getCurrentTrack());

        service.updateCurrentPlaylistQueue(
                List.of(first, second, third)
        );

        service.playNextTrack();

        assertEquals(third, service.getCurrentTrack());
    }

    @Test
    void updateCurrentPlaylistQueue_OnLastTrack_ShouldUseNewTrackInsteadOfStopping() {

        Track first = createTrack("A");
        Track second = createTrack("B");

        service.playTrack(first, List.of(first));

        service.updateCurrentPlaylistQueue(
                List.of(first, second)
        );

        service.playNextTrack();

        assertEquals(second, service.getCurrentTrack());
    }

    @Test
    void updateCurrentPlaylistQueue_ShouldNotChangeCurrentTrack() {

        Track first = createTrack("A");
        Track second = createTrack("B");

        service.playTrack(first, List.of(first));

        service.updateCurrentPlaylistQueue(
                List.of(first, second)
        );

        assertEquals(first, service.getCurrentTrack());
    }

    @Test
    void stop_ShouldResetCurrentIndex() {

        Track first = createTrack("A");
        Track second = createTrack("B");

        service.playTrack(first, List.of(first, second));

        service.stop();

        assertEquals(0, service.getCurrentIndex());
    }

    @Test
    void playTrack_ShouldInitializeCurrentIndex() {

        Track first = createTrack("A");
        Track second = createTrack("B");

        service.playTrack(second, List.of(first, second));

        assertEquals(1, service.getCurrentIndex());
    }

    @Test
    void playNextTrack_ShouldUpdateCurrentIndex() {

        Track first = createTrack("A");
        Track second = createTrack("B");

        service.playTrack(first, List.of(first, second));

        service.playNextTrack();

        assertEquals(1, service.getCurrentIndex());
    }
}*/