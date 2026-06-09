package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;

import java.util.List;

public class SequentialPlaylistNavigationStrategy implements PlaylistNavigationStrategy {

    @Override
    public Playlist getNextPlaylist(List<Playlist> playlists, Playlist currentPlaylist) {
        if (playlists == null || playlists.isEmpty() || currentPlaylist == null) {
            return null;
        }

        int currentIndex = playlists.indexOf(currentPlaylist);

        if (currentIndex == -1) {
            return null;
        }

        int nextIndex = currentIndex + 1;

        if (nextIndex >= playlists.size()) {
            return playlists.get(0);
        }

        return playlists.get(nextIndex);
    }
}