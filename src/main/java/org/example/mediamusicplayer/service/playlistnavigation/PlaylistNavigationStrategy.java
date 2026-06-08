package org.example.mediamusicplayer.service.playlistnavigation;

import org.example.mediamusicplayer.model.Playlist;

import java.util.List;

//ogni strategia riceve la lista delle playlist e la playlist corrente, poi restituisce la prossima playlist.
public interface PlaylistNavigationStrategy {

    Playlist getNextPlaylist(List<Playlist> playlists, Playlist currentPlaylist);
}