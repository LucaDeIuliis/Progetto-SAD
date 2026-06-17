package org.example.mediamusicplayer.service.command;

import org.example.mediamusicplayer.model.*;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

class DeleteTrackCommandTest {

    private MusicLibrary library;
    private MusicLibraryService libraryService;
    private PlaylistService playlistService;

    private Track track;
    private Playlist playlist;


    @BeforeEach
    void setUp() {

        library = new MusicLibrary();
        libraryService = new MusicLibraryService();
        playlistService = new PlaylistService();


        track = new Track(
                "Song",
                "Artist",
                Duration.ofSeconds(200),
                "Rock",
                Year.of(2024)
        );


        playlist = new Playlist("Rock Playlist");


        library.getAllTracks().add(track);
        library.getPlaylists().add(playlist);
    }


    @Test
    void execute_GlobalDelete_ShouldRemoveTrackFromLibrary() {

        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        track,
                        null,
                        null,
                        library,
                        libraryService,
                        playlistService
                );


        command.execute();


        assertFalse(
                library.getAllTracks().contains(track)
        );
    }


    @Test
    void undo_GlobalDelete_ShouldRestoreTrack() {

        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        track,
                        null,
                        null,
                        library,
                        libraryService,
                        playlistService
                );


        command.execute();
        command.undo();


        assertTrue(
                library.getAllTracks().contains(track)
        );
    }


    @Test
    void execute_DeleteFromPlaylist_ShouldRemoveTrackOnlyFromPlaylist() {

        playlist.getTracks().add(track);


        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        track,
                        playlist,
                        null,
                        library,
                        libraryService,
                        playlistService
                );


        command.execute();


        assertFalse(
                playlist.getTracks().contains(track)
        );


        // la traccia rimane nella libreria
        assertTrue(
                library.getAllTracks().contains(track)
        );
    }


    @Test
    void undo_DeleteFromPlaylist_ShouldRestoreTrack() {

        playlist.getTracks().add(track);


        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        track,
                        playlist,
                        null,
                        library,
                        libraryService,
                        playlistService
                );


        command.execute();
        command.undo();


        assertTrue(
                playlist.getTracks().contains(track)
        );
    }


    @Test
    void execute_DeleteTag_ShouldRemoveOnlyTag() {

        track.addTag(TrackTag.FAVOURITE);


        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        track,
                        playlist,
                        TrackTag.FAVOURITE,
                        library,
                        libraryService,
                        playlistService
                );


        command.execute();


        assertFalse(
                track.getTags().contains(TrackTag.FAVOURITE)
        );
    }


    @Test
    void undo_DeleteTag_ShouldRestoreTag() {

        track.addTag(TrackTag.FAVOURITE);


        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        track,
                        playlist,
                        TrackTag.FAVOURITE,
                        library,
                        libraryService,
                        playlistService
                );


        command.execute();

        command.undo();


        assertTrue(
                track.getTags().contains(TrackTag.FAVOURITE)
        );
    }


    @Test
    void globalDeleteUndo_ShouldRestorePlaylistAssociation() {

        playlist.getTracks().add(track);


        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        track,
                        null,
                        null,
                        library,
                        libraryService,
                        playlistService
                );


        command.execute();
        command.undo();


        assertTrue(
                library.getAllTracks().contains(track)
        );

        assertTrue(
                playlist.getTracks().contains(track)
        );
    }
}
