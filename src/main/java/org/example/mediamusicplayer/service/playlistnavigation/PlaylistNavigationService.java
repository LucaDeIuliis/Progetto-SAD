package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.service.playback.PlaybackMode;

import java.util.List;

public class PlaylistNavigationService {

    public Playlist getNextPlaylist(
            List<Playlist> playlists,
            Playlist currentPlaylist,
            PlaybackMode playbackMode
    ) {
        PlaylistNavigationStrategy strategy =
                PlaylistNavigationStrategyFactory.create(
                        playbackMode
                );

        return strategy.getNextPlaylist(
                playlists,
                currentPlaylist
        );
    }
}