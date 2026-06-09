package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShufflePlaylistNavigationStrategyTest {

    @Test
    void getNextPlaylist_SinglePlaylist() {

        Playlist p1 = new Playlist("P1");

        ShufflePlaylistNavigationStrategy strategy =
                new ShufflePlaylistNavigationStrategy();

        assertEquals(
                p1,
                strategy.getNextPlaylist(
                        List.of(p1),
                        p1
                )
        );
    }

    @Test
    void getNextPlaylist_ShouldReturnDifferentPlaylist() {

        Playlist p1 = new Playlist("P1");
        Playlist p2 = new Playlist("P2");

        ShufflePlaylistNavigationStrategy strategy =
                new ShufflePlaylistNavigationStrategy();

        Playlist result =
                strategy.getNextPlaylist(
                        List.of(p1, p2),
                        p1
                );

        assertNotNull(result);
        assertNotEquals(p1, result);
    }
}