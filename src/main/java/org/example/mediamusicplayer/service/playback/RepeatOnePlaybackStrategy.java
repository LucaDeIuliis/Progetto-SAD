package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;

import java.util.List;

// Questa strategia ripete sempre la traccia corrente.

public class RepeatOnePlaybackStrategy implements PlaybackStrategy {

    @Override
    public Track getNextTrack(List<Track> tracks, Track currentTrack) {
        return currentTrack;
    }
}