package org.example.mediamusicplayer.service;


import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistServiceTest {

    private PlaylistService playlistService;
    private MusicLibrary library;

    @BeforeEach
    void setUp() {
        playlistService = new PlaylistService();
        library = new MusicLibrary();
    }

    @Test
    void createPlaylist_ValidName_ShouldCreatePlaylist() {

        Playlist playlist = playlistService.createPlaylist(
                "Rock Classics",
                library
        );

        assertNotNull(playlist);
        assertEquals("Rock Classics", playlist.getName());
    }

    @Test
    void createPlaylist_EmptyName_ShouldThrowException() {

        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.createPlaylist("", library)
        );
    }

    @Test
    void createPlaylist_DuplicateName_ShouldThrowException() {

        library.getPlaylists().add(new Playlist("Rock"));

        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.createPlaylist("Rock", library)
        );
    }

    @Test
    void renamePlaylist_ShouldRenamePlaylist() {

        Playlist playlist = new Playlist("Old Name");

        library.getPlaylists().add(playlist);

        playlistService.renamePlaylist(
                playlist,
                "New Name",
                library
        );

        assertEquals("New Name", playlist.getName());
    }

    @Test
    void renamePlaylist_DuplicateName_ShouldThrowException() {

        Playlist p1 = new Playlist("Rock");
        Playlist p2 = new Playlist("Pop");

        library.getPlaylists().addAll(p1, p2);

        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.renamePlaylist(
                        p2,
                        "Rock",
                        library
                )
        );
    }

    @Test
    void addTrackToPlaylist_ShouldAddTrack() {

        Playlist playlist = new Playlist("My Playlist");

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2020)
        );

        playlistService.addTrackToPlaylist(playlist, track);

        assertTrue(playlist.getTracks().contains(track));
    }

    @Test
    void addTrackToPlaylist_DuplicateTrack_ShouldThrowException() {

        Playlist playlist = new Playlist("My Playlist");

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2020)
        );

        playlist.addTrack(track);

        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.addTrackToPlaylist(
                        playlist,
                        track
                )
        );
    }

    @Test
    void removeTrackFromPlaylist_ShouldRemoveTrack() {

        Playlist playlist = new Playlist("My Playlist");

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2020)
        );

        playlist.addTrack(track);

        playlistService.removeTrackFromPlaylist(
                playlist,
                track
        );

        assertFalse(playlist.getTracks().contains(track));
    }

    @Test
    void removeTrackFromPlaylist_TrackNotPresent_ShouldThrowException() {

        Playlist playlist = new Playlist("My Playlist");

        Track track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Pop",
                Year.of(2020)
        );

        assertThrows(
                PlaylistValidationException.class,
                () -> playlistService.removeTrackFromPlaylist(
                        playlist,
                        track
                )
        );
    }
}