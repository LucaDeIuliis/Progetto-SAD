package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class TrackServiceTest {

    private TrackService trackService;
    private MusicLibrary library;

    @BeforeEach
    void setUp() {
        trackService = new TrackService();
        library = new MusicLibrary();
    }

    @Test
    void createTrack_ValidData_ShouldCreateTrack() {

        Track track = trackService.createTrack(
                "Bohemian Rhapsody",
                "Queen",
                "5:55",
                "Rock",
                "1975",
                library
        );

        assertNotNull(track);
        assertEquals("Bohemian Rhapsody", track.getTitle());
        assertEquals("Queen", track.getAuthor());
        assertEquals("Rock", track.getGenre());
        assertEquals(Year.of(1975), track.getYear());
        assertEquals(Duration.ofSeconds(355), track.getLength());
    }

    @Test
    void createTrack_EmptyTitle_ShouldThrowException() {

        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "",
                        "Queen",
                        "5:55",
                        "Rock",
                        "1975",
                        library
                )
        );
    }

    @Test
    void createTrack_InvalidYear_ShouldThrowException() {

        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song",
                        "Artist",
                        "3:30",
                        "Pop",
                        "abcd",
                        library
                )
        );
    }

    @Test
    void createTrack_FutureYear_ShouldThrowException() {

        int futureYear = Year.now().getValue() + 1;

        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song",
                        "Artist",
                        "3:30",
                        "Pop",
                        String.valueOf(futureYear),
                        library
                )
        );
    }

    @Test
    void createTrack_InvalidDuration_ShouldThrowException() {

        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song",
                        "Artist",
                        "3:99",
                        "Pop",
                        "2020",
                        library
                )
        );
    }

    @Test
    void createTrack_DuplicateTrack_ShouldThrowException() {

        Track existing = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2020)
        );

        library.getAllTracks().add(existing);

        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song",
                        "Artist",
                        "3:20",
                        "Rock",
                        "2022",
                        library
                )
        );
    }

    @Test
    void updateTrack_ShouldUpdateTrackData() {

        Track track = new Track(
                "Old",
                "Old Author",
                Duration.ofSeconds(120),
                "Pop",
                Year.of(2010)
        );

        library.getAllTracks().add(track);

        trackService.updateTrack(
                track,
                "New Title",
                "New Author",
                "4:00",
                "Rock",
                "2020",
                library
        );

        assertEquals("New Title", track.getTitle());
        assertEquals("New Author", track.getAuthor());
        assertEquals("Rock", track.getGenre());
        assertEquals(Year.of(2020), track.getYear());
        assertEquals(Duration.ofSeconds(240), track.getLength());
    }

    @Test
    void updateTrack_NullTrack_ShouldThrowException() {

        assertThrows(
                TrackValidationException.class,
                () -> trackService.updateTrack(
                        null,
                        "Title",
                        "Author",
                        "3:00",
                        "Rock",
                        "2020",
                        library
                )
        );
    }

    @Test
    void createTrack_ZeroDuration_ShouldThrowException() {

        assertThrows(
                TrackValidationException.class,
                () -> trackService.createTrack(
                        "Song",
                        "Artist",
                        "0",
                        "Rock",
                        "2020",
                        library
                )
        );
    }

    @Test
    void createTrack_DurationInSeconds_ShouldCreateTrack() {

        Track track = trackService.createTrack(
                "Song",
                "Artist",
                "225",
                "Rock",
                "2020",
                library
        );

        assertEquals(
                Duration.ofSeconds(225),
                track.getLength()
        );
    }
    @Test
    void updateTrack_DuplicateTrack_ShouldThrowException() {

        Track track1 = new Track(
                "Song1",
                "Artist1",
                Duration.ofSeconds(100),
                "Pop",
                Year.of(2020)
        );

        Track track2 = new Track(
                "Song2",
                "Artist2",
                Duration.ofSeconds(100),
                "Pop",
                Year.of(2020)
        );

        library.getAllTracks().add(track1);
        library.getAllTracks().add(track2);

        assertThrows(
                TrackValidationException.class,
                () -> trackService.updateTrack(
                        track2,
                        "Song1",
                        "Artist1",
                        "200",
                        "Rock",
                        "2022",
                        library
                )
        );
    }
    @Test
    void updateTrack_SameTrack_ShouldNotBeConsideredDuplicate() {

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(100),
                "Pop",
                Year.of(2020)
        );

        library.getAllTracks().add(track);

        assertDoesNotThrow(
                () -> trackService.updateTrack(
                        track,
                        "Song",
                        "Artist",
                        "200",
                        "Rock",
                        "2021",
                        library
                )
        );
    }
}