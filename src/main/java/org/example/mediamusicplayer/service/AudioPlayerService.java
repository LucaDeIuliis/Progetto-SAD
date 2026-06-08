package org.example.mediamusicplayer.service;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.example.mediamusicplayer.service.playback.PlaybackMode;
import org.example.mediamusicplayer.service.playback.PlaybackStrategyFactory;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.playback.PlaybackStrategy;
import org.example.mediamusicplayer.service.playback.SequentialPlaybackStrategy;

import java.util.List;

public class AudioPlayerService {

    private Track tracciaAttuale;
    private List<Track> playlistCorrente;
    private int secondiTrascorsi = 0;
    private Timeline timeline;
    private int currentIndex = 0; // memorizza l'indice della traccia in riproduzione
    private PlaybackStrategy playbackStrategy;

    // Callbacks per comunicare con il Controller e aggiornare l'interfaccia
    private Runnable onTimeUpdate;
    private Runnable onTrackFinished;
    private Runnable onTrackChanged;

    public AudioPlayerService() {
        this.playbackStrategy = new SequentialPlaybackStrategy();
    }

    public void setOnTimeUpdate(Runnable onTimeUpdate) {
        this.onTimeUpdate = onTimeUpdate;
    }

    public void setOnTrackFinished(Runnable onTrackFinished) {
        this.onTrackFinished = onTrackFinished;
    }

    public void setOnTrackChanged(Runnable onTrackChanged) {
        this.onTrackChanged = onTrackChanged;
    }

    public void setPlaybackStrategy(PlaybackStrategy playbackStrategy) {
        if (playbackStrategy != null) {
            this.playbackStrategy = playbackStrategy;
        }
    }

    public void setPlaybackMode(PlaybackMode mode) {
        this.playbackStrategy = PlaybackStrategyFactory.create(mode);
    }

    public Track getCurrentTrack() {
        return tracciaAttuale;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void playTrack(Track track) {
        playTrack(track, null);
    }

    public void playTrack(Track track, List<Track> tracks) {

        stopTimelineOnly();

        this.tracciaAttuale = track;
        this.playlistCorrente = tracks;
        this.secondiTrascorsi = 0;

        if (tracks != null) {
            this.currentIndex = tracks.indexOf(track);

            if (this.currentIndex < 0) {
                this.currentIndex = 0;
            }
        } else {
            this.currentIndex = 0;
        }

        startTimelineForCurrentTrack();

        if (onTrackChanged != null) {
            onTrackChanged.run();
        }

        if (onTimeUpdate != null) {
            onTimeUpdate.run();
        }
    }

    private void startTimelineForCurrentTrack() {
        if (tracciaAttuale == null) {
            return;
        }

        long durataTotale = tracciaAttuale.getLength().getSeconds();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondiTrascorsi++;

            if (onTimeUpdate != null) {
                onTimeUpdate.run();
            }

            if (secondiTrascorsi >= durataTotale) {
                playNextTrack();
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void playNextTrack() {
        stopTimelineOnly();

        Track prossimaTraccia = null;

        if (playbackStrategy != null && playlistCorrente != null) {
            prossimaTraccia = playbackStrategy.getNextTrack(playlistCorrente, tracciaAttuale);
        }

        if (prossimaTraccia == null) {
            tracciaAttuale = null;
            secondiTrascorsi = 0;
            currentIndex = 0;

            if (onTimeUpdate != null) {
                onTimeUpdate.run();
            }

            if (onTrackFinished != null) {
                onTrackFinished.run();
            }

            return;
        }

        tracciaAttuale = prossimaTraccia;
        secondiTrascorsi = 0;

        if (playlistCorrente != null) {
            currentIndex = playlistCorrente.indexOf(prossimaTraccia);
        }

        startTimelineForCurrentTrack();

        if (onTrackChanged != null) {
            onTrackChanged.run();
        }

        if (onTimeUpdate != null) {
            onTimeUpdate.run();
        }
    }

    public void pause() {
        if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
        }
    }

    public void resume() {
        if (timeline != null && timeline.getStatus() == Animation.Status.PAUSED) {
            timeline.play();
        }
    }

    public void stop() {
        stopTimelineOnly();
        tracciaAttuale = null;
        secondiTrascorsi = 0;
        currentIndex = 0;

        if (onTimeUpdate != null) {
            onTimeUpdate.run();
        }
    }

    private void stopTimelineOnly() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    // US-14: aggiorna dinamicamente la coda attiva senza interrompere la riproduzione.
    public void updateCurrentPlaylistQueue(List<Track> updatedPlaylist) {

        if (updatedPlaylist == null || updatedPlaylist.isEmpty()) {
            return;
        }

        Track currentTrack = this.tracciaAttuale;

        this.playlistCorrente = updatedPlaylist;

        if (currentTrack != null) {

            int newIndex = updatedPlaylist.indexOf(currentTrack);

            if (newIndex >= 0) {
                this.currentIndex = newIndex;
            }
        }
    }

    public boolean isPlaying() {
        return timeline != null && timeline.getStatus() == Animation.Status.RUNNING;
    }

    public boolean isPaused() {
        return timeline != null && timeline.getStatus() == Animation.Status.PAUSED;
    }

    public String getFormattedCurrentTime() {
        return String.format("%d:%02d", secondiTrascorsi / 60, secondiTrascorsi % 60);
    }
}