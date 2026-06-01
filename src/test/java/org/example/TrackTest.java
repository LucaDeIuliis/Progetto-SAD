package org.example;
import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Year;
import static org.junit.jupiter.api.Assertions.*;

/*
Questi test coprono:
-corretta inizializzazione tramite costruttore;
-corretto funzionamento di getter e setter;
-formattazione della durata con Duration;
-caso limite length == null;
-durata superiore a un’ora;
-output del metodo toString().
*/

class TrackTest {
    @Test
    void constructorShouldInitializeAllFieldsCorrectly() {
        Track track = new Track(
                "Nel blu dipinto di blu",
                "Domenico Modugno",
                Duration.ofMinutes(3).plusSeconds(35),
                "Musica italiana",
                Year.of(1958)
        );
        assertEquals("Nel blu dipinto di blu", track.getTitle());
        assertEquals("Domenico Modugno", track.getAuthor());
        assertEquals(Duration.ofMinutes(3).plusSeconds(35), track.getLength());
        assertEquals("Musica italiana", track.getGenre());
        assertEquals(Year.of(1958), track.getYear());
    }
    @Test
    void settersShouldUpdateAllFieldsCorrectly() {
        Track track = new Track(
                "Vecchio titolo",
                "Vecchio autore",
                Duration.ofMinutes(3),
                "Vecchio genere",
                Year.of(2000)
        );
        track.setTitle("Nuovo titolo");
        track.setAuthor("Nuovo autore");
        track.setLength(Duration.ofMinutes(4).plusSeconds(30));
        track.setGenre("Pop italiano");
        track.setYear(Year.of(2024));
        assertEquals("Nuovo titolo", track.getTitle());
        assertEquals("Nuovo autore", track.getAuthor());
        assertEquals(Duration.ofMinutes(4).plusSeconds(30), track.getLength());
        assertEquals("Pop italiano", track.getGenre());
        assertEquals(Year.of(2024), track.getYear());
    }
    @Test
    void getFormattedLengthShouldReturnMinutesAndSecondsWithTwoDigits() {
        Track track = new Track(
                "Canzone breve",
                "Artista italiano",
                Duration.ofMinutes(3).plusSeconds(7),
                "Pop",
                Year.of(2020)
        );
        assertEquals("3:07", track.getFormattedLength());
    }
    @Test
    void getFormattedLengthShouldReturnZeroWhenLengthIsNull() {
        Track track = new Track(
                "Canzone senza durata",
                "Artista sconosciuto",
                null,
                "Genere sconosciuto",
                Year.of(2020)
        );
        assertEquals("0:00", track.getFormattedLength());
    }
    @Test
    void getFormattedLengthShouldUseTotalMinutesWhenDurationIsLongerThanOneHour() {
        Track track = new Track(
                "Traccia lunga",
                "Orchestra italiana",
                Duration.ofHours(1).plusMinutes(2).plusSeconds(5),
                "Classica",
                Year.of(1999)
        );
        assertEquals("62:05", track.getFormattedLength());
    }
    @Test
    void getFormattedLengthShouldFormatExactMinutesCorrectly() {
        Track track = new Track(
                "Canzone da quattro minuti",
                "Cantautore italiano",
                Duration.ofMinutes(4),
                "Jazz",
                Year.of(2010)
        );
        assertEquals("4:00", track.getFormattedLength());
    }
    @Test
    void toStringShouldReturnReadableTrackRepresentation() {
        Track track = new Track(
                "La cura",
                "Franco Battiato",
                Duration.ofMinutes(4).plusSeconds(3),
                "Musica d'autore",
                Year.of(1996)
        );
        assertEquals(
                "La cura - Franco Battiato [4:03] (1996)",
                track.toString()
        );
    }
    @Test
    void toStringShouldUseFormattedLengthWhenLengthIsNull() {
        Track track = new Track(
                "Canzone sconosciuta",
                "Autore sconosciuto",
                null,
                "Genere sconosciuto",
                Year.of(2024)
        );
        assertEquals(
                "Canzone sconosciuta - Autore sconosciuto [0:00] (2024)",
                track.toString()
        );
    }
}