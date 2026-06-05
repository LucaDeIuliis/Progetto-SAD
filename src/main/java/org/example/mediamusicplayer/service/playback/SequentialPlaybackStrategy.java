package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;

import java.util.List;

// Questa strategia riproduce la traccia successiva in ordine.
// Se la traccia corrente è l’ultima, restituisce null.

public class SequentialPlaybackStrategy implements PlaybackStrategy {

    @Override
    public Track getNextTrack(List<Track> tracks, Track currentTrack) {
        if (tracks == null || tracks.isEmpty() || currentTrack == null) {
            return null;
        }

        int currentIndex = tracks.indexOf(currentTrack);

        if (currentIndex == -1) {
            return null;
        }

        int nextIndex = currentIndex + 1;

        if (nextIndex >= tracks.size()) {
            return null;
        }

        return tracks.get(nextIndex);
    }
}