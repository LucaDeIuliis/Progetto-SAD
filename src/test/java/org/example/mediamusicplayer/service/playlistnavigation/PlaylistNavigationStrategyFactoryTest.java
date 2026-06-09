package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.service.playback.PlaybackMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PlaylistNavigationStrategyFactoryTest {

    @Test
    void create_Null_ShouldReturnSequential() {

        assertInstanceOf(
                SequentialPlaylistNavigationStrategy.class,
                PlaylistNavigationStrategyFactory.create(null)
        );
    }

    @Test
    void create_Sequential() {

        assertInstanceOf(
                SequentialPlaylistNavigationStrategy.class,
                PlaylistNavigationStrategyFactory.create(
                        PlaybackMode.SEQUENTIAL
                )
        );
    }

    @Test
    void create_Shuffle() {

        assertInstanceOf(
                ShufflePlaylistNavigationStrategy.class,
                PlaylistNavigationStrategyFactory.create(
                        PlaybackMode.SHUFFLE
                )
        );
    }

    @Test
    void create_RepeatOne() {

        assertInstanceOf(
                RepeatOnePlaylistNavigationStrategy.class,
                PlaylistNavigationStrategyFactory.create(
                        PlaybackMode.REPEAT_ONE
                )
        );
    }
}