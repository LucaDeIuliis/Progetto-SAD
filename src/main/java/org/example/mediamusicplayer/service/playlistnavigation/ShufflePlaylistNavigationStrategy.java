package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShufflePlaylistNavigationStrategy implements PlaylistNavigationStrategy {

    private final Random random = new Random();

    @Override
    public Playlist getNextPlaylist(List<Playlist> playlists, Playlist currentPlaylist) {
        if (playlists == null || playlists.isEmpty()) {
            return null;
        }

        if (playlists.size() == 1) {
            return playlists.get(0);
        }

        List<Playlist> candidates = new ArrayList<>(playlists);
        candidates.remove(currentPlaylist);

        return candidates.get(random.nextInt(candidates.size()));
    }
}