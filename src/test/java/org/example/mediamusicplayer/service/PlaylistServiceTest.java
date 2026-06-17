package org.example.mediamusicplayer.service;

import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistServiceTest {

    private PlaylistService service;
    private MusicLibrary library;
    private Playlist playlist;
    private Track track1;
    private Track track2;

    @BeforeEach
    void setUp(){
        service=new PlaylistService();
        library=new MusicLibrary();
        playlist=new Playlist("Rock");

        track1=new Track(
                "Song 1",
                "Artist",
                Duration.ofSeconds(200),
                "Rock",
                Year.of(2024)
        );

        track2=new Track(
                "Song 2",
                "Artist",
                Duration.ofSeconds(180),
                "Pop",
                Year.of(2020)
        );

        library.getPlaylists().add(playlist);
        library.getAllTracks().add(track1);
        library.getAllTracks().add(track2);
    }

    @Test
    void shouldCreatePlaylist(){
        Playlist nuova=
                service.createPlaylist(
                        " Nuova Playlist ",
                        library
                );

        assertEquals(
                "Nuova Playlist",
                nuova.getName()
        );
    }

    @Test
    void shouldNotCreateDuplicatePlaylist(){

        assertThrows(
                PlaylistValidationException.class,
                ()->service.createPlaylist(
                        "rock",
                        library
                )
        );
    }

    @Test
    void shouldRenamePlaylist(){

        service.renamePlaylist(
                playlist,
                "Nuovo",
                library
        );

        assertEquals(
                "Nuovo",
                playlist.getName()
        );
    }

    @Test
    void shouldAddTrack(){

        service.addTrackToPlaylist(
                playlist,
                track1
        );

        assertTrue(
                playlist.getTracks()
                        .contains(track1)
        );
    }

    @Test
    void shouldNotAddDuplicateTrack(){

        playlist.getTracks().add(track1);

        assertThrows(
                PlaylistValidationException.class,
                ()->service.addTrackToPlaylist(
                        playlist,
                        track1
                )
        );
    }

    @Test
    void shouldRemoveTrack(){

        playlist.getTracks().add(track1);

        service.removeTrackFromPlaylist(
                playlist,
                track1
        );

        assertFalse(
                playlist.getTracks()
                        .contains(track1)
        );
    }

    @Test
    void shouldCreateAutomaticPlaylist(){

        Playlist auto =
                service.createAutomaticPlaylist(
                        "Genere",
                        "Rock",
                        library,
                        new MusicLibraryService()
                );

        assertTrue(
                auto.isGenerataAutomaticamente()
        );

        assertTrue(
                auto.getTracks()
                        .contains(track1)
        );

        assertFalse(
                auto.getTracks()
                        .contains(track2)
        );
    }

    @Test
    void shouldRespectGenreFilter(){

        playlist.setGenerataAutomaticamente(true);
        playlist.setTipoFiltro("Genere");
        playlist.setFiltroAutomatico("Rock");

        assertTrue(
                service.trackRispettaFiltro(
                        playlist,
                        track1
                )
        );

        assertFalse(
                service.trackRispettaFiltro(
                        playlist,
                        track2
                )
        );
    }

    @Test
    void shouldMoveTrack(){

        List<Track> tracks=new ArrayList<>();

        tracks.add(track1);
        tracks.add(track2);

        service.moveTrack(
                tracks,
                0,
                1
        );

        assertEquals(
                track2,
                tracks.get(0)
        );
    }

    @Test
    void shouldDetectSmartPlaylist(){

        Playlist smart=
                new Playlist(
                        "I Miei Preferiti ❤"
                );

        assertTrue(
                service.isSmartPlaylist(smart)
        );
    }

    @Test
    void shouldApplyTag(){

        Playlist smart=
                new Playlist(
                        "I Miei Preferiti ❤"
                );

        service.applyPlaylistTagToTrack(
                smart,
                track1
        );

        assertTrue(
                track1.getTags()
                        .contains(
                                TrackTag.FAVOURITE
                        )
        );
    }
}