package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SequentialPlaylistNavigationStrategyTest {

    @Test
    void getNextPlaylist_ShouldReturnNextPlaylist() {

        Playlist p1 = new Playlist("P1");
        Playlist p2 = new Playlist("P2");

        SequentialPlaylistNavigationStrategy strategy =
                new SequentialPlaylistNavigationStrategy();

        assertEquals(
                p2,
                strategy.getNextPlaylist(
                        List.of(p1, p2),
                        p1
                )
        );
    }

    @Test
    void getNextPlaylist_LastPlaylist_ShouldReturnNull() {

        Playlist p1 = new Playlist("P1");

        SequentialPlaylistNavigationStrategy strategy =
                new SequentialPlaylistNavigationStrategy();

        assertNull(
                strategy.getNextPlaylist(
                        List.of(p1),
                        p1
                )
        );
    }
}