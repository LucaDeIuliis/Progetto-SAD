package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;

import java.util.List;

public class RepeatOnePlaylistNavigationStrategy implements PlaylistNavigationStrategy {

    @Override
    public Playlist getNextPlaylist(List<Playlist> playlists, Playlist currentPlaylist) {
        return currentPlaylist;
    }
}