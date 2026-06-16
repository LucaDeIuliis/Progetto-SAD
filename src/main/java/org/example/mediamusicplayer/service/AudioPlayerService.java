package org.example.mediamusicplayer.service;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.playback.PlaybackMode;
import org.example.mediamusicplayer.service.playback.PlaybackObserver;
import org.example.mediamusicplayer.service.playback.PlaybackStrategy;
import org.example.mediamusicplayer.service.playback.PlaybackStrategyFactory;
import org.example.mediamusicplayer.service.playback.SequentialPlaybackStrategy;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayerService {

    private Track tracciaAttuale;
    private List<Track> playlistCorrente;
    private int secondiTrascorsi = 0;
    private Timeline timeline;
    private boolean tracciaCorrenteConteggiata = false;
    private PlaybackStrategy playbackStrategy;

    /*
     * OBSERVER PATTERN:
     * AudioPlayerService è il Subject.
     * I PlaybackObserver registrati vengono notificati quando:
     * - cambia il tempo di riproduzione;
     * - cambia la traccia corrente;
     * - termina la riproduzione.
     */
    private final List<PlaybackObserver> observers = new ArrayList<>();

    public AudioPlayerService() {
        this.playbackStrategy = new SequentialPlaybackStrategy();
    }

    // =========================================================
    // OBSERVER PATTERN
    // =========================================================

    public void addObserver(PlaybackObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    private void notifyTimeUpdate() {
        for (PlaybackObserver observer : observers) {
            observer.onTimeUpdate(getFormattedCurrentTime(), tracciaAttuale);
        }
    }

    private void notifyTrackChanged() {
        for (PlaybackObserver observer : observers) {
            observer.onTrackChanged(tracciaAttuale);
        }
    }

    private void notifyTrackHalfPlayed() {
        for (PlaybackObserver observer : observers) {
            observer.onTrackHalfPlayed(tracciaAttuale);
        }
    }

    private void notifyPlaybackFinished() {
        for (PlaybackObserver observer : observers) {
            observer.onPlaybackFinished();
        }
    }

    // =========================================================
    // STRATEGY / FACTORY PATTERN
    // =========================================================

    public void setPlaybackMode(PlaybackMode mode) {
        this.playbackStrategy = PlaybackStrategyFactory.create(mode);
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public Track getCurrentTrack() {
        return tracciaAttuale;
    }

    public String getFormattedCurrentTime() {
        return String.format("%d:%02d", secondiTrascorsi / 60, secondiTrascorsi % 60);
    }

    public boolean isPlaying() {
        return timeline != null && timeline.getStatus() == Animation.Status.RUNNING;
    }

    public boolean isPaused() {
        return timeline != null && timeline.getStatus() == Animation.Status.PAUSED;
    }

    // =========================================================
    // PLAYBACK LOGIC
    // =========================================================

    /*
     * Questo metodo riceve anche la lista corrente perché lo Strategy Pattern
     * deve conoscere l'insieme delle tracce da cui scegliere la prossima.
     */
    public void playTrack(Track track, List<Track> tracks) {
        stopTimelineOnly();

        this.tracciaAttuale = track;
        this.playlistCorrente = tracks;
        this.secondiTrascorsi = 0;
        this.tracciaCorrenteConteggiata = false;

        startTimelineForCurrentTrack();

        notifyTrackChanged();
        notifyTimeUpdate();
    }

    private void startTimelineForCurrentTrack() {
        if (tracciaAttuale == null) {
            return;
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondiTrascorsi++;

            notifyTimeUpdate();

            long durataDinamicaAggiornata = tracciaAttuale.getLength().getSeconds();

            long metaDurata = durataDinamicaAggiornata / 2;

            if (!tracciaCorrenteConteggiata && secondiTrascorsi >= metaDurata) {
                tracciaCorrenteConteggiata = true;
                notifyTrackHalfPlayed();
            }

            if (secondiTrascorsi >= durataDinamicaAggiornata) {
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

            notifyTimeUpdate();
            notifyPlaybackFinished();

            return;
        }

        tracciaAttuale = prossimaTraccia;
        secondiTrascorsi = 0;
        tracciaCorrenteConteggiata = false;

        startTimelineForCurrentTrack();

        notifyTrackChanged();
        notifyTimeUpdate();
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

        notifyTimeUpdate();
    }

    private void stopTimelineOnly() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}