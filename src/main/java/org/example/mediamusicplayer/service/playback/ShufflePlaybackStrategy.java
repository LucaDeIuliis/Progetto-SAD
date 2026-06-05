package org.example.mediamusicplayer.service.playback;

import org.example.mediamusicplayer.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Questa strategia sceglie una traccia casuale diversa da quella corrente.

public class ShufflePlaybackStrategy implements PlaybackStrategy {

    private final Random random = new Random();

    @Override
    public Track getNextTrack(List<Track> tracks, Track currentTrack) {
        if (tracks == null || tracks.isEmpty()) {
            return null;
        }

        if (tracks.size() == 1) {
            return tracks.get(0);
        }

        List<Track> candidates = new ArrayList<>(tracks);
        candidates.remove(currentTrack);

        return candidates.get(random.nextInt(candidates.size()));
    }
}