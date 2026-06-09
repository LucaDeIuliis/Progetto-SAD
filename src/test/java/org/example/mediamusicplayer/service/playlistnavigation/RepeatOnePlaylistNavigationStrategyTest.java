package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RepeatOnePlaylistNavigationStrategyTest {

    @Test
    void getNextPlaylist_ShouldReturnCurrentPlaylist() {

        Playlist playlist = new Playlist("Rock");

        RepeatOnePlaylistNavigationStrategy strategy =
                new RepeatOnePlaylistNavigationStrategy();

        assertEquals(
                playlist,
                strategy.getNextPlaylist(
                        null,
                        playlist
                )
        );
    }
}