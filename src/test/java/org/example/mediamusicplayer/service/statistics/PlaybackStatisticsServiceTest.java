package org.example.mediamusicplayer.service.statistics;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaybackStatisticsServiceTest {

    @Test
    void registerTrackPlayback_ShouldIncrementCounter() {

        PlaybackStatisticsService service =
                new PlaybackStatisticsService();

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(210),
                "Rock",
                Year.of(2024)
        );

        assertEquals(0, track.getPlayCount());

        service.registerTrackPlayback(track);

        assertEquals(1, track.getPlayCount());

        service.registerTrackPlayback(track);

        assertEquals(2, track.getPlayCount());
    }


    @Test
    void registerPlaylistPlayback_ShouldIncrementCounter() {

        PlaybackStatisticsService service =
                new PlaybackStatisticsService();

        Playlist playlist =
                new Playlist("Rock Playlist");

        assertEquals(0, playlist.getPlayCount());

        service.registerPlaylistPlayback(playlist);

        assertEquals(1, playlist.getPlayCount());

        service.registerPlaylistPlayback(playlist);

        assertEquals(2, playlist.getPlayCount());
    }


    @Test
    void getMostPlayedTracks_ShouldReturnSortedTracks() {

        PlaybackStatisticsService service =
                new PlaybackStatisticsService();

        MusicLibrary library =
                new MusicLibrary();


        Track first = new Track(
                "First",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2024)
        );


        Track second = new Track(
                "Second",
                "Artist",
                Duration.ofSeconds(220),
                "Rock",
                Year.of(2023)
        );


        library.getAllTracks().add(first);
        library.getAllTracks().add(second);


        service.registerTrackPlayback(first);
        service.registerTrackPlayback(first);

        service.registerTrackPlayback(second);


        List<Track> result =
                service.getMostPlayedTracks(library, 10);


        assertEquals(2, result.size());

        assertEquals(
                "First",
                result.get(0).getTitle()
        );
    }


    @Test
    void getMostPlayedPlaylists_ShouldReturnSortedPlaylists() {

        PlaybackStatisticsService service =
                new PlaybackStatisticsService();


        MusicLibrary library =
                new MusicLibrary();


        Playlist first =
                new Playlist("Playlist A");

        Playlist second =
                new Playlist("Playlist B");


        library.getPlaylists().add(first);
        library.getPlaylists().add(second);


        service.registerPlaylistPlayback(first);
        service.registerPlaylistPlayback(first);

        service.registerPlaylistPlayback(second);


        List<Playlist> result =
                service.getMostPlayedPlaylists(library, 10);


        assertEquals(2, result.size());

        assertEquals(
                "Playlist A",
                result.get(0).getName()
        );
    }


    @Test
    void getMostPlayedTracks_ShouldRespectLimit() {

        PlaybackStatisticsService service =
                new PlaybackStatisticsService();

        MusicLibrary library =
                new MusicLibrary();


        Track track1 = new Track(
                "A",
                "Artist",
                Duration.ofSeconds(100),
                "Pop",
                Year.of(2024)
        );

        Track track2 = new Track(
                "B",
                "Artist",
                Duration.ofSeconds(100),
                "Rock",
                Year.of(2024)
        );


        library.getAllTracks().add(track1);
        library.getAllTracks().add(track2);


        service.registerTrackPlayback(track1);
        service.registerTrackPlayback(track2);


        List<Track> result =
                service.getMostPlayedTracks(library, 1);


        assertEquals(1, result.size());
    }


    @Test
    void nullValues_ShouldNotCrash() {

        PlaybackStatisticsService service =
                new PlaybackStatisticsService();


        assertDoesNotThrow(() ->
                service.registerTrackPlayback(null)
        );


        assertDoesNotThrow(() ->
                service.registerPlaylistPlayback(null)
        );


        assertTrue(
                service.getMostPlayedTracks(null, 10).isEmpty()
        );


        assertTrue(
                service.getMostPlayedPlaylists(null, 10).isEmpty()
        );
    }
}