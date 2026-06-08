package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.service.playback.PlaybackMode;

public class PlaylistNavigationStrategyFactory {

    private PlaylistNavigationStrategyFactory() {
        // Classe utility: impedisce l'istanziazione
    }

    public static PlaylistNavigationStrategy create(PlaybackMode mode) {
        if (mode == null) {
            return new SequentialPlaylistNavigationStrategy();
        }

        return switch (mode) {
            case SEQUENTIAL -> new SequentialPlaylistNavigationStrategy();
            case SHUFFLE -> new ShufflePlaylistNavigationStrategy();
            case REPEAT_ONE -> new RepeatOnePlaylistNavigationStrategy();
        };
    }
}