package org.example.mediamusicplayer.service.command;

import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class AssignTrackCommandTest {

    private Playlist playlist;
    private Track track;
    private PlaylistService playlistService;
    private AssignTrackCommand command;

    @BeforeEach
    void setUp() {

        playlist = new Playlist("Rock Playlist");

        track = new Track(
                "Bohemian Rhapsody",
                "Queen",
                Duration.ofSeconds(355),
                "Rock",
                Year.of(1975)
        );

        playlistService = new PlaylistService();

        command = new AssignTrackCommand(
                playlist,
                track,
                playlistService
        );
    }


    @Test
    void execute_ShouldAddTrackToPlaylist() {

        command.execute();

        assertTrue(
                playlist.getTracks().contains(track)
        );
    }


    @Test
    void undo_ShouldRemoveTrackFromPlaylist() {

        command.execute();

        assertTrue(
                playlist.getTracks().contains(track)
        );

        command.undo();

        assertFalse(
                playlist.getTracks().contains(track)
        );
    }


    @Test
    void undoWithoutExecute_ShouldNotModifyPlaylist() {

        command.undo();

        assertTrue(
                playlist.getTracks().isEmpty()
        );
    }


    @Test
    void executeTwice_ShouldNotBreakPlaylist() {

        command.execute();
        command.execute();

        assertEquals(
                1,
                playlist.getTracks().size()
        );
    }
}
