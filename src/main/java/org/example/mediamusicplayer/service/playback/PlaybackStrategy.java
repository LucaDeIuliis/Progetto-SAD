package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;

import java.util.List;

public interface PlaybackStrategy {

    Track getNextTrack(List<Track> tracks, Track currentTrack);
}