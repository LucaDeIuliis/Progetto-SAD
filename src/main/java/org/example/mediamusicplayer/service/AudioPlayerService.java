package org.example.mediamusicplayer.service;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration; // Nota: Questo è il Duration di JavaFX, non di java.time
import org.example.mediamusicplayer.model.Track;

public class AudioPlayerService {

    private Track tracciaAttuale;
    private int secondiTrascorsi = 0;
    private Timeline timeline;

    // Callbacks per comunicare con il Controller e aggiornare l'interfaccia
    private Runnable onTimeUpdate;
    private Runnable onTrackFinished;

    public void setOnTimeUpdate(Runnable onTimeUpdate) { this.onTimeUpdate = onTimeUpdate; }
    public void setOnTrackFinished(Runnable onTrackFinished) { this.onTrackFinished = onTrackFinished; }

    public Track getCurrentTrack() { return tracciaAttuale; }

    public void playTrack(Track track) {
        stop(); // Resetta se c'era un'altra canzone
        this.tracciaAttuale = track;
        this.secondiTrascorsi = 0;

        long durataTotale = track.getLength().getSeconds();

        // Creiamo un timer che scatta ogni 1 secondo
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondiTrascorsi++;

            // Aggiorna l'interfaccia grafica
            if (onTimeUpdate != null) onTimeUpdate.run();

            // Se la canzone è finita
            if (secondiTrascorsi >= durataTotale) {
                stop();
                if (onTrackFinished != null) onTrackFinished.run();
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        if (onTimeUpdate != null) onTimeUpdate.run();
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
        if (timeline != null) {
            timeline.stop();
        }
        secondiTrascorsi = 0;
        if (onTimeUpdate != null) onTimeUpdate.run();
    }

    public boolean isPlaying() {
        return timeline != null && timeline.getStatus() == Animation.Status.RUNNING;
    }

    public boolean isPaused() {
        return timeline != null && timeline.getStatus() == Animation.Status.PAUSED;
    }

    // Calcola la stringa del tempo attuale (es. "1:05")
    public String getFormattedCurrentTime() {
        return String.format("%d:%02d", secondiTrascorsi / 60, secondiTrascorsi % 60);
    }
}