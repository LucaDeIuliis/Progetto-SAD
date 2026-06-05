package org.example.mediamusicplayer.service.playback;

public class PlaybackStrategyFactory {

    private PlaybackStrategyFactory() {
        // Classe utility: impedisce l'istanziazione
    }

    public static PlaybackStrategy create(PlaybackMode mode) {
        if (mode == null) {
            return new SequentialPlaybackStrategy();
        }

        return switch (mode) {
            case SEQUENTIAL -> new SequentialPlaybackStrategy();
            case SHUFFLE -> new ShufflePlaybackStrategy();
            case REPEAT_ONE -> new RepeatOnePlaybackStrategy();
        };
    }
}