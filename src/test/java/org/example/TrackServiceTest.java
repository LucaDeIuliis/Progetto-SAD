package org.example;

import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.TrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class TrackServiceTest {

    private TrackService trackService;

    @BeforeEach
    void setUp() {
        trackService = new TrackService();
    }

    @Test
    void createTrackShouldCreateTrackWithSeconds() {
        Track track = trackService.createTrack(
                "Imagine",
                "John Lennon",
                "183",
                "Pop",
                "1971"
        );

        assertEquals("Imagine", track.getTitle());
        assertEquals("John Lennon", track.getAuthor());
        assertEquals("Pop", track.getGenre());
        assertEquals(Year.of(1971), track.getYear());
        assertEquals(Duration.ofSeconds(183), track.getLength());
    }

    @Test
    void createTrackShouldCreateTrackWithMinutesAndSeconds() {
        Track track = trackService.createTrack(
                "Imagine",
                "John Lennon",
                "3:03",
                "Pop",
                "1971"
        );

        assertEquals(Duration.ofSeconds(183), track.getLength());
    }

    @Test
    void createTrackShouldThrowWhenTitleIsNull() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        null, "Author", "120", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenAuthorIsEmpty() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "", "120", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenGenreIsEmpty() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "120", "", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenYearIsEmpty() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "120", "Pop", "")
        );
    }

    @Test
    void createTrackShouldThrowWhenLengthIsEmpty() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenYearIsNotNumeric() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "120", "Pop", "abcd")
        );
    }

    @Test
    void createTrackShouldThrowWhenYearIsInFuture() {
        int futureYear = Year.now().plusYears(1).getValue();

        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "120", "Pop",
                        String.valueOf(futureYear))
        );
    }

    @Test
    void createTrackShouldThrowWhenDurationContainsLetters() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "abc", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenSecondsAreGreaterThan59() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "3:75", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenMinutesAreNegative() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "-3:20", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenDurationIsZero() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "0", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenDurationIsNegative() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "-100", "Pop", "2020")
        );
    }

    @Test
    void createTrackShouldThrowWhenDurationFormatIsWrong() {
        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song", "Author", "3:20:10", "Pop", "2020")
        );
    }
}