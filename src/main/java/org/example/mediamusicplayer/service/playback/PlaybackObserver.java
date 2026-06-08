package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;

public interface PlaybackObserver {

    void onTimeUpdate(String currentTime, Track currentTrack);

    void onTrackChanged(Track currentTrack);

    void onPlaybackFinished();
}