package org.example.mediamusicplayer.service.playback;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PlaybackStrategyFactoryTest {

    @Test
    void create_Null_ShouldReturnSequential() {

        assertInstanceOf(
                SequentialPlaybackStrategy.class,
                PlaybackStrategyFactory.create(null)
        );
    }

    @Test
    void create_Sequential() {

        assertInstanceOf(
                SequentialPlaybackStrategy.class,
                PlaybackStrategyFactory.create(
                        PlaybackMode.SEQUENTIAL
                )
        );
    }

    @Test
    void create_Shuffle() {

        assertInstanceOf(
                ShufflePlaybackStrategy.class,
                PlaybackStrategyFactory.create(
                        PlaybackMode.SHUFFLE
                )
        );
    }

    @Test
    void create_RepeatOne() {

        assertInstanceOf(
                RepeatOnePlaybackStrategy.class,
                PlaybackStrategyFactory.create(
                        PlaybackMode.REPEAT_ONE
                )
        );
    }
}