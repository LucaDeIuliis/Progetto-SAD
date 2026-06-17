package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrackServiceTest {

    private TrackService service;
    private MusicLibrary library;
    private Track track;

    @BeforeEach
    void setUp() {
        service = new TrackService();
        library = new MusicLibrary();

        track = new Track(
                "Song",
                "Artist",
                java.time.Duration.ofSeconds(200),
                "Rock",
                java.time.Year.of(2024)
        );

        library.getAllTracks().add(track);
    }


    @Test
    void shouldCreateTrack() {

        Track nuova =
                service.createTrack(
                        "Nuova",
                        "Autore",
                        "3:30",
                        "Pop",
                        "2023",
                        library
                );

        assertEquals(
                "Nuova",
                nuova.getTitle()
        );

        assertEquals(
                "Autore",
                nuova.getAuthor()
        );

        assertEquals(
                "3:30",
                nuova.getFormattedLength()
        );
    }


    @Test
    void shouldCreateTrackWithSeconds() {

        Track nuova =
                service.createTrack(
                        "Test",
                        "Artist",
                        "180",
                        "Rock",
                        "2022",
                        library
                );

        assertEquals(
                "3:00",
                nuova.getFormattedLength()
        );
    }


    @Test
    void shouldRejectEmptyTitle() {

        assertThrows(
                TrackValidationException.class,
                () -> service.createTrack(
                        "",
                        "Artist",
                        "3:00",
                        "Rock",
                        "2024",
                        library
                )
        );
    }


    @Test
    void shouldRejectDuplicateTrack() {

        assertThrows(
                TrackValidationException.class,
                () -> service.createTrack(
                        "Song",
                        "Artist",
                        "2:00",
                        "Pop",
                        "2024",
                        library
                )
        );
    }


    @Test
    void shouldRejectWrongYear() {

        assertThrows(
                TrackValidationException.class,
                () -> service.createTrack(
                        "Future",
                        "Artist",
                        "3:00",
                        "Rock",
                        "3000",
                        library
                )
        );
    }


    @Test
    void shouldRejectWrongDuration() {

        assertThrows(
                TrackValidationException.class,
                () -> service.createTrack(
                        "Test",
                        "Artist",
                        "abc",
                        "Rock",
                        "2024",
                        library
                )
        );
    }


    @Test
    void shouldUpdateTrack() {

        service.updateTrack(
                track,
                "Nuovo Titolo",
                "Nuovo Autore",
                "4:00",
                "Pop",
                "2020",
                library
        );


        assertEquals(
                "Nuovo Titolo",
                track.getTitle()
        );

        assertEquals(
                "Pop",
                track.getGenre()
        );

        assertEquals(
                "4:00",
                track.getFormattedLength()
        );
    }


    @Test
    void shouldNotUpdateDuplicate() {

        Track altra =
                new Track(
                        "Altra",
                        "A",
                        java.time.Duration.ofSeconds(100),
                        "Rock",
                        java.time.Year.of(2022)
                );

        library.getAllTracks().add(altra);


        assertThrows(
                TrackValidationException.class,
                () -> service.updateTrack(
                        track,
                        "Altra",
                        "A",
                        "3:00",
                        "Pop",
                        "2024",
                        library
                )
        );
    }


    @Test
    void shouldUpdateFavouriteTag() {

        service.updateTags(
                track,
                true,
                false,
                false
        );

        assertTrue(
                track.getTags()
                        .contains(
                                TrackTag.FAVOURITE
                        )
        );
    }


    @Test
    void shouldUpdateMultipleTags() {

        service.updateTags(
                track,
                true,
                true,
                true
        );

        assertTrue(
                track.getTags()
                        .contains(TrackTag.FAVOURITE)
        );

        assertTrue(
                track.getTags()
                        .contains(TrackTag.EXPLICIT)
        );

        assertTrue(
                track.getTags()
                        .contains(TrackTag.NEW_RELEASE)
        );
    }


    @Test
    void shouldClearTagsWhenFalse() {

        service.updateTags(
                track,
                true,
                true,
                true
        );

        service.updateTags(
                track,
                false,
                false,
                false
        );

        assertTrue(
                track.getTags().isEmpty()
        );
    }
}