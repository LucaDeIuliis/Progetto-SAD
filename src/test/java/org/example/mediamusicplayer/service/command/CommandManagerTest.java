package org.example.mediamusicplayer.service.command;

import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    private CommandManager commandManager;
    private Playlist playlist;
    private Track track;
    private PlaylistService playlistService;


    @BeforeEach
    void setUp() {

        commandManager = new CommandManager();

        playlist = new Playlist("Playlist Test");

        track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(180),
                "Pop",
                Year.of(2024)
        );

        playlistService = new PlaylistService();
    }


    @Test
    void executeCommand_ShouldSaveCommandInHistory() {

        AssignTrackCommand command =
                new AssignTrackCommand(
                        playlist,
                        track,
                        playlistService
                );


        commandManager.executeCommand(command);


        assertTrue(
                playlist.getTracks().contains(track)
        );
    }


    @Test
    void undoLastCommand_ShouldUndoLastOperation() {

        AssignTrackCommand command =
                new AssignTrackCommand(
                        playlist,
                        track,
                        playlistService
                );


        commandManager.executeCommand(command);

        boolean result =
                commandManager.undoLastCommand();


        assertTrue(result);

        assertFalse(
                playlist.getTracks().contains(track)
        );
    }


    @Test
    void undoWithoutCommands_ShouldReturnFalse() {

        boolean result =
                commandManager.undoLastCommand();


        assertFalse(result);
    }


    @Test
    void clearHistory_ShouldRemoveCommands() {

        AssignTrackCommand command =
                new AssignTrackCommand(
                        playlist,
                        track,
                        playlistService
                );


        commandManager.executeCommand(command);

        commandManager.clearHistory();


        assertFalse(
                commandManager.undoLastCommand()
        );

        // la traccia resta perché lo storico è stato cancellato
        assertTrue(
                playlist.getTracks().contains(track)
        );
    }


    @Test
    void multipleCommands_ShouldUndoInReverseOrder() {

        Track secondTrack = new Track(
                "Second Song",
                "Artist",
                Duration.ofSeconds(200),
                "Rock",
                Year.of(2023)
        );


        AssignTrackCommand first =
                new AssignTrackCommand(
                        playlist,
                        track,
                        playlistService
                );

        AssignTrackCommand second =
                new AssignTrackCommand(
                        playlist,
                        secondTrack,
                        playlistService
                );


        commandManager.executeCommand(first);
        commandManager.executeCommand(second);


        commandManager.undoLastCommand();


        assertFalse(
                playlist.getTracks().contains(secondTrack)
        );

        assertTrue(
                playlist.getTracks().contains(track)
        );
    }
}
