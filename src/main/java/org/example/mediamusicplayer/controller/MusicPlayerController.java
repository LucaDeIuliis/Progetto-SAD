package org.example.mediamusicplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.AudioPlayerService;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.util.AlertUtil;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.service.playback.PlaybackMode;
import java.time.Year;
import java.util.Optional;

public class MusicPlayerController {

    @FXML private ListView<Playlist> playlistListView;
    @FXML private TextField newPlaylistInput;
    @FXML private Label currentPlaylistLabel;
    @FXML private ComboBox<Playlist> playlistComboBox;

    @FXML private Button playPauseButton;
    @FXML private Button skipButton;
    @FXML private Label timeLabel;
    @FXML private ComboBox<PlaybackMode> playbackModeComboBox;
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, String> lengthColumn;
    @FXML private TableColumn<Track, String> genreColumn;
    @FXML private TableColumn<Track, Year> yearColumn;

    @FXML private TextField titleInput;
    @FXML private TextField authorInput;
    @FXML private TextField lengthInput;
    @FXML private TextField genreInput;
    @FXML private TextField yearInput;

    private MusicLibrary libreria;
    private Playlist playlistAttuale;

    private TrackService trackService;
    private PlaylistService playlistService;
    private MusicLibraryService libraryService;
    private AudioPlayerService audioPlayerService;

    @FXML
    public void initialize() {
        trackService = new TrackService();
        playlistService = new PlaylistService();
        libraryService = new MusicLibraryService();
        audioPlayerService = new AudioPlayerService();
        libreria = new MusicLibrary();

        playbackModeComboBox.getItems().setAll(PlaybackMode.values());
        playbackModeComboBox.setValue(PlaybackMode.SEQUENTIAL);

        audioPlayerService.setPlaybackMode(PlaybackMode.SEQUENTIAL);

        playbackModeComboBox.valueProperty().addListener((obs, oldMode, newMode) -> {
            if (newMode != null) {
                audioPlayerService.setPlaybackMode(newMode);
            }
        });

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLength"));

        playlistListView.setItems(libreria.getPlaylists());
        playlistComboBox.setItems(libreria.getPlaylists());
        trackTable.setItems(libreria.getAllTracks());

        // DOPPIO CLICK SULLA TABELLA PER RIPRODURRE
        trackTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
                if (tracciaSelezionata != null) {
                    audioPlayerService.playTrack(tracciaSelezionata, getCurrentTrackList());
                    playPauseButton.setText("⏸ PAUSA");
                    playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
                    skipButton.setVisible(true);
                    skipButton.setManaged(true);
                }
            }
        });

        // Aggiornamento timer (con il Titolo per non perdere mai l'orientamento)
        audioPlayerService.setOnTimeUpdate(() -> {
            Track tracciaInRiproduzione = audioPlayerService.getCurrentTrack();
            if (tracciaInRiproduzione != null) {
                String current = audioPlayerService.getFormattedCurrentTime();
                String total = tracciaInRiproduzione.getFormattedLength();
                timeLabel.setText(tracciaInRiproduzione.getTitle() + " - " + current + " / " + total);
            } else {
                timeLabel.setText("0:00 / 0:00");
            }
        });

        audioPlayerService.setOnTrackFinished(() -> {
            skipButton.setVisible(false);
            skipButton.setManaged(false);
            playPauseButton.setText("▶ PLAY");
            playPauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        });

        // === BUG FIX AUTOPLAY: Proteggiamo l'aggiornamento visivo della tabella ===
        audioPlayerService.setOnTrackChanged(() -> {
            Track currentTrack = audioPlayerService.getCurrentTrack();
            if (currentTrack != null) {
                // Esegue il cambio selezione SOLO se la schermata attuale contiene davvero la canzone
                // Altrimenti lascia la musica andare senza generare errori visivi!
                if (trackTable.getItems().contains(currentTrack)) {
                    trackTable.getSelectionModel().select(currentTrack);
                    trackTable.scrollTo(currentTrack);
                }

                playPauseButton.setText("⏸ PAUSA");
                playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
                skipButton.setVisible(true);
                skipButton.setManaged(true);
            }
        });

        // CAMBIO PLAYLIST
        playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                playlistAttuale = nuova;
                currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
                trackTable.setItems(playlistAttuale.getTracks());
                clearTrackInputs();
            }
        });

        // SELEZIONE TABELLA (Non avvia più l'audio da sola, riempie solo i campi)
        trackTable.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                titleInput.setText(nuova.getTitle());
                authorInput.setText(nuova.getAuthor());
                genreInput.setText(nuova.getGenre());
                yearInput.setText(String.valueOf(nuova.getYear().getValue()));
                long totalSeconds = nuova.getLength().getSeconds();
                lengthInput.setText(String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60));

                if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
                    timeLabel.setText("0:00 / " + nuova.getFormattedLength());
                }
            } else {
                if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
                    timeLabel.setText("0:00 / 0:00");
                }
            }
        });
    }

    private void clearTrackInputs() {
        titleInput.clear();
        authorInput.clear();
        lengthInput.clear();
        genreInput.clear();
        yearInput.clear();
        trackTable.getSelectionModel().clearSelection();
    }

    private java.util.List<Track> getCurrentTrackList() {
        return new ArrayList<>(trackTable.getItems());
    }

    // === COMANDI AUDIO ===
    @FXML
    public void onPlayPauseClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        Track tracciaCorrente = audioPlayerService.getCurrentTrack();

        if (tracciaSelezionata == null && tracciaCorrente == null) {
            AlertUtil.showError("Nessuna traccia", "Seleziona prima una traccia dalla tabella per riprodurla.");
            return;
        }

        if (tracciaSelezionata == null) {
            tracciaSelezionata = tracciaCorrente;
        }

        if (!tracciaSelezionata.equals(tracciaCorrente)) {
            audioPlayerService.playTrack(tracciaSelezionata, getCurrentTrackList());
            playPauseButton.setText("⏸ PAUSA");
            skipButton.setVisible(true);
            skipButton.setManaged(true);
            playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
            return;
        }

        if (audioPlayerService.isPlaying()) {
            audioPlayerService.pause();
            playPauseButton.setText("▶ PLAY");
            playPauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else if (audioPlayerService.isPaused()) {
            audioPlayerService.resume();
            playPauseButton.setText("⏸ PAUSA");
            playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            audioPlayerService.playTrack(tracciaSelezionata, getCurrentTrackList());
            playPauseButton.setText("⏸ PAUSA");
            skipButton.setVisible(true);
            skipButton.setManaged(true);
            playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    // === BUG FIX SKIP: Semplificato drasticamente! ===
    @FXML
    public void onSkipClick() {
        // Il Controller non fa più nessun calcolo con la tabella grafica.
        // Chiede semplicemente al Service (che ha la coda salvata) di mandare avanti la traccia!
        if (audioPlayerService.getCurrentTrack() != null) {
            audioPlayerService.playNextTrack();
        }
    }

    @FXML
    public void onStopClick() {
        if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
            AlertUtil.showError("Audio già fermo", "Non c'è nessuna traccia in riproduzione o in pausa da fermare.");
            return;
        }

        audioPlayerService.stop();
        skipButton.setVisible(false);
        skipButton.setManaged(false);
        playPauseButton.setText("▶ PLAY");
        playPauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        if (tracciaSelezionata != null) {
            timeLabel.setText("0:00 / " + tracciaSelezionata.getFormattedLength());
        } else {
            timeLabel.setText("0:00 / 0:00");
        }
    }

    // === COMANDI GESTIONALI ===
    @FXML
    public void onViewAllTracksClick() {
        playlistAttuale = null;
        playlistListView.getSelectionModel().clearSelection();
        trackTable.setItems(libreria.getAllTracks());
        currentPlaylistLabel.setText("Gestione Tracce: Tutte le canzoni");
        clearTrackInputs();
    }

    @FXML
    public void onAddPlaylistClick() {
        try {
            Playlist nuovaPlaylist = playlistService.createPlaylist(newPlaylistInput.getText(), libreria);
            libraryService.addPlaylist(libreria, nuovaPlaylist);
            newPlaylistInput.clear();
            playlistListView.getSelectionModel().select(nuovaPlaylist);
        } catch (PlaylistValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onRenamePlaylistClick() {
        Playlist playlistSelezionata = playlistListView.getSelectionModel().getSelectedItem();
        if (playlistSelezionata == null) {
            AlertUtil.showError("Nessuna selezione", "Seleziona la playlist da rinominare.");
            return;
        }

        Optional<String> result = AlertUtil.askInput(
                "Rinomina Playlist",
                "Stai modificando: " + playlistSelezionata.getName(),
                "Inserisci il nuovo nome:",
                playlistSelezionata.getName()
        );

        result.ifPresent(nuovoNome -> {
            try {
                playlistService.renamePlaylist(playlistSelezionata, nuovoNome, libreria);
                playlistListView.refresh();
                playlistComboBox.setItems(null);
                playlistComboBox.setItems(libreria.getPlaylists());
                if (playlistAttuale == playlistSelezionata) {
                    currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistSelezionata.getName());
                }
            } catch (PlaylistValidationException e) {
                AlertUtil.showError(e.getHeader(), e.getMessage());
            }
        });
    }

    @FXML
    public void onAddTrackClick() {
        try {
            Track nuovaTraccia = trackService.createTrack(
                    titleInput.getText(), authorInput.getText(),
                    lengthInput.getText(), genreInput.getText(), yearInput.getText(),
                    libreria
            );

            libraryService.addTrackToLibrary(libreria, nuovaTraccia);

            if (playlistAttuale != null) {
                playlistService.addTrackToPlaylist(playlistAttuale, nuovaTraccia);
            }
            clearTrackInputs();

        } catch (TrackValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onUpdateTrackClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        if (tracciaSelezionata == null) {
            AlertUtil.showError("Nessuna selezione", "Seleziona una traccia dalla tabella per modificarla.");
            return;
        }

        try {
            trackService.updateTrack(
                    tracciaSelezionata, titleInput.getText(), authorInput.getText(),
                    lengthInput.getText(), genreInput.getText(), yearInput.getText(),
                    libreria
            );

            trackTable.refresh();
            clearTrackInputs();

        } catch (TrackValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onAssignToPlaylistClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        Playlist playlistScelta = playlistComboBox.getValue();

        if (tracciaSelezionata == null) {
            AlertUtil.showError("Nessuna Traccia", "Seleziona prima una traccia.");
            return;
        }
        if (playlistScelta == null) {
            AlertUtil.showError("Nessuna Playlist", "Seleziona una playlist.");
            return;
        }

        try {
            playlistService.addTrackToPlaylist(playlistScelta, tracciaSelezionata);
            AlertUtil.showInfo("Fatto!", "Traccia aggiunta a " + playlistScelta.getName());
        } catch (PlaylistValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onDeletePlaylistClick() {
        Playlist playlistSelezionata = playlistListView.getSelectionModel().getSelectedItem();
        if (playlistSelezionata == null) {
            AlertUtil.showError("Nessuna selezione", "Seleziona la playlist che vuoi eliminare dalla barra laterale.");
            return;
        }

        libraryService.deletePlaylist(libreria, playlistSelezionata);
        if (playlistAttuale == playlistSelezionata) onViewAllTracksClick();
    }

    @FXML
    public void onDeleteTrackClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        if (tracciaSelezionata == null) {
            AlertUtil.showError("Nessuna selezione", "Clicca su una traccia prima di eliminarla.");
            return;
        }

        if (tracciaSelezionata.equals(audioPlayerService.getCurrentTrack())) {
            audioPlayerService.stop();
            playPauseButton.setText("▶ PLAY");
            playPauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        }

        if (playlistAttuale == null) {
            libraryService.deleteTrackGlobal(libreria, tracciaSelezionata);
        } else {
            playlistService.removeTrackFromPlaylist(playlistAttuale, tracciaSelezionata);
        }

        clearTrackInputs();
    }
}