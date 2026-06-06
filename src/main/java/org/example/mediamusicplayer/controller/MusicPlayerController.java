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

        // Gestione aggiornamento timer simulato a schermo
        audioPlayerService.setOnTimeUpdate(() -> {
            if (audioPlayerService.getCurrentTrack() != null) {
                String current = audioPlayerService.getFormattedCurrentTime();
                String total = audioPlayerService.getCurrentTrack().getFormattedLength();
                timeLabel.setText(current + " / " + total);
            } else {
                timeLabel.setText("0:00 / 0:00");
            }
        });

        // === LOGICA AUTOPLAY QUANDO FINISCE LA TRACCIA ===
        audioPlayerService.setOnTrackFinished(() -> {
            Track tracciaFinita = audioPlayerService.getCurrentTrack();

            // Cerchiamo l'indice della traccia che è appena terminata
            int indiceAttuale = trackTable.getItems().indexOf(tracciaFinita);

            // Controlliamo se la traccia esiste e SE NON è l'ultima della lista
            if (indiceAttuale != -1 && indiceAttuale < trackTable.getItems().size() - 1) {

                int prossimoIndice = indiceAttuale + 1;
                Track prossimaTraccia = trackTable.getItems().get(prossimoIndice);

                // Selezioniamo graficamente la riga successiva (questo aggiornerà anche i campi input)
                trackTable.getSelectionModel().select(prossimoIndice);

                // Facciamo partire la nuova canzone
                audioPlayerService.playTrack(prossimaTraccia);

                // Manteniamo il bottone nello stato "PAUSA" (colore giallo)
                playPauseButton.setText("⏸ PAUSA");
                playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");

            } else {
                skipButton.setVisible(false);
                skipButton.setManaged(false);
                // Se eravamo all'ultima traccia, la riproduzione si ferma
                playPauseButton.setText("▶ PLAY");
                playPauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
            }
        });

        // Questa callback serve quando una traccia finisce e AudioPlayerService
        // passa automaticamente alla successiva tramite Strategy
        audioPlayerService.setOnTrackChanged(() -> {
            Track currentTrack = audioPlayerService.getCurrentTrack();

            if (currentTrack != null) {
                trackTable.getSelectionModel().select(currentTrack);
                trackTable.scrollTo(currentTrack);
                playPauseButton.setText("⏸ PAUSA");
                playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
            }
        });

        // ASCOLTATORE CAMBIO PLAYLIST
        playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                playlistAttuale = nuova;
                currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
                trackTable.setItems(playlistAttuale.getTracks());

                // Svuotiamo i campi quando cambiamo playlist!
                clearTrackInputs();
            }
        });

        trackTable.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                titleInput.setText(nuova.getTitle());
                authorInput.setText(nuova.getAuthor());
                genreInput.setText(nuova.getGenre());
                yearInput.setText(String.valueOf(nuova.getYear().getValue()));
                long totalSeconds = nuova.getLength().getSeconds();
                lengthInput.setText(String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60));

                if (audioPlayerService.isPlaying()) {
                    audioPlayerService.playTrack(nuova, getCurrentTrackList());
                    playPauseButton.setText("⏸ PAUSA");
                    playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
                } else {
                    timeLabel.setText("0:00 / " + nuova.getFormattedLength());
                }
            } else {
                if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
                    timeLabel.setText("0:00 / 0:00");
                }
            }
        });
    }

    // === METODO DI UTILITA' PER PULIRE IL CODICE E I CAMPI ===
    private void clearTrackInputs() {
        titleInput.clear();
        authorInput.clear();
        lengthInput.clear();
        genreInput.clear();
        yearInput.clear();
        trackTable.getSelectionModel().clearSelection();
    }

    // Questo metodo restituisce la lista delle tracce attualmente visibili nella tabella
    private java.util.List<Track> getCurrentTrackList() {
        return new ArrayList<>(trackTable.getItems());
    }

    @FXML
    public void onPlayPauseClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();

        if (tracciaSelezionata == null) {
            AlertUtil.showError("Nessuna traccia", "Seleziona prima una traccia dalla tabella per riprodurla.");
            return;
        }

        if (!tracciaSelezionata.equals(audioPlayerService.getCurrentTrack())) {
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

    @FXML
    public void onSkipClick() {

        if (trackTable.getItems().isEmpty()) {
            return;
        }

        Track tracciaCorrente = audioPlayerService.getCurrentTrack();

        if (tracciaCorrente == null) {
            return;
        }

        int indiceAttuale = trackTable.getItems().indexOf(tracciaCorrente);

        if (indiceAttuale == -1) {
            return;
        }

        int prossimoIndice = indiceAttuale + 1;

        if (prossimoIndice >= trackTable.getItems().size()) {

            audioPlayerService.stop();

            skipButton.setVisible(false);
            skipButton.setManaged(false);

            playPauseButton.setText("▶ PLAY");
            playPauseButton.setStyle(
                    "-fx-background-color: #4CAF50; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 14px;"
            );

            return;
        }

        Track prossimaTraccia = trackTable.getItems().get(prossimoIndice);

        trackTable.getSelectionModel().select(prossimoIndice);
        trackTable.scrollTo(prossimoIndice);

        audioPlayerService.playTrack(prossimaTraccia, getCurrentTrackList());

        playPauseButton.setText("⏸ PAUSA");
        playPauseButton.setStyle(
                "-fx-background-color: #FFC107; " +
                        "-fx-text-fill: black; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px;"
        );
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

    @FXML
    public void onViewAllTracksClick() {
        playlistAttuale = null;
        playlistListView.getSelectionModel().clearSelection();
        trackTable.setItems(libreria.getAllTracks());
        currentPlaylistLabel.setText("Gestione Tracce: Tutte le canzoni");

        // Svuotiamo i campi quando torniamo a Tutte le Tracce!
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

            // Usiamo il nuovo metodo pulito!
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
            // Usiamo il nuovo metodo pulito!
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

        audioPlayerService.stop();
        playPauseButton.setText("▶ PLAY");
        playPauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        if (playlistAttuale == null) {
            libraryService.deleteTrackGlobal(libreria, tracciaSelezionata);
        } else {
            playlistService.removeTrackFromPlaylist(playlistAttuale, tracciaSelezionata);
        }

        // Usiamo il nuovo metodo pulito!
        clearTrackInputs();
    }
}