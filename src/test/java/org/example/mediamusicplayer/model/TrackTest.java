package org.example.mediamusicplayer.model;


import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

    @Test
    void getFormattedLength_ShouldFormatCorrectly() {

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(225),
                "Rock",
                Year.of(2020)
        );

        assertEquals("3:45", track.getFormattedLength());
    }

    @Test
    void getFormattedLength_NullDuration_ShouldReturnZero() {

        Track track = new Track(
                "Song",
                "Artist",
                null,
                "Rock",
                Year.of(2020)
        );

        assertEquals("0:00", track.getFormattedLength());
    }

    @Test
    void toString_ShouldContainTrackInfo() {

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(225),
                "Rock",
                Year.of(2020)
        );

        String result = track.toString();

        assertTrue(result.contains("Song"));
        assertTrue(result.contains("Artist"));
        assertTrue(result.contains("3:45"));
        assertTrue(result.contains("2020"));
    }

    @Test
    void setters_ShouldUpdateValues() {

        Track track = new Track(
                "A",
                "B",
                Duration.ofSeconds(100),
                "Pop",
                Year.of(2010)
        );

        track.setTitle("New");
        track.setAuthor("Author");
        track.setGenre("Rock");
        track.setYear(Year.of(2024));
        track.setLength(Duration.ofSeconds(300));

        assertEquals("New", track.getTitle());
        assertEquals("Author", track.getAuthor());
        assertEquals("Rock", track.getGenre());
        assertEquals(Year.of(2024), track.getYear());
        assertEquals(Duration.ofSeconds(300), track.getLength());
    }

    @Test
    void getFormattedLength_ShouldHandleLongTracks() {

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(3665),
                "Rock",
                Year.of(2020)
        );

        assertEquals("61:05", track.getFormattedLength());
    }

    @Test
    void constructor_ShouldSetFields() {

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Rock",
                Year.of(2020)
        );

        assertEquals("Song", track.getTitle());
        assertEquals("Artist", track.getAuthor());
        assertEquals("Rock", track.getGenre());
        assertEquals(Year.of(2020), track.getYear());
        assertEquals(Duration.ofSeconds(200), track.getLength());
    }
}