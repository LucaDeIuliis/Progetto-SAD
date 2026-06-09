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
import org.example.mediamusicplayer.service.playlistnavigation.PlaylistNavigationStrategy;
import org.example.mediamusicplayer.service.playlistnavigation.PlaylistNavigationStrategyFactory;
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
import org.example.mediamusicplayer.service.playback.PlaybackObserver;

import java.time.Year;
import java.util.Optional;

    public class MusicPlayerController implements PlaybackObserver {

        @FXML private ListView<Playlist> playlistListView;
        @FXML private TextField newPlaylistInput;
        @FXML private Label currentPlaylistLabel;
        @FXML private ComboBox<Playlist> playlistComboBox;

        @FXML private Button playPauseButton;
        @FXML private Button skipButton;
        @FXML private Button skipPlaylistButton;
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

            /*
             * OBSERVER PATTERN:
             * Il controller si registra come observer del service.
             * Da questo momento AudioPlayerService notificherà il controller
             * tramite i metodi onTimeUpdate, onTrackChanged e onPlaybackFinished.
             */
            audioPlayerService.addObserver(this);

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
                        setPauseButtonState();
                        showSkipButton();
                    }
                }
            });

            // ASCOLTATORE CAMBIO PLAYLIST
            playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
                if (nuova != null) {
                    playlistAttuale = nuova;
                    currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
                    trackTable.setItems(playlistAttuale.getTracks());
                    showSkipPlaylistButton();
                    clearTrackInputs();
                }
            });

            // ASCOLTATORE SELEZIONE SINGOLA
            // Riempie i campi, ma non disturba la musica.
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
            return trackTable.getItems();
        }

        private void setPlayButtonState() {
            playPauseButton.setText("▶ PLAY");
            playPauseButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        }

        private void setPauseButtonState() {
            playPauseButton.setText("⏸ PAUSA");
            playPauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14px;");
        }

        private void showSkipButton() {
            skipButton.setVisible(true);
            skipButton.setManaged(true);
        }

        private void hideSkipButton() {
            skipButton.setVisible(false);
            skipButton.setManaged(false);
        }

        // =========================================================
        // OBSERVER PATTERN
        // =========================================================

        @Override
        public void onTimeUpdate(String currentTime, Track currentTrack) {
            if (currentTrack != null) {
                timeLabel.setText(
                        currentTrack.getTitle() + " - " + currentTime + " / " + currentTrack.getFormattedLength()
                );
            } else {
                timeLabel.setText("0:00 / 0:00");
            }
        }

        @Override
        public void onTrackChanged(Track currentTrack) {
            if (currentTrack != null) {
                /*
                 * Bug fix mantenuto:
                 * non forziamo la selezione della tabella con select(),
                 * così non cancelliamo o alteriamo i campi di modifica.
                 * Se la traccia è visibile nella tabella corrente, facciamo solo scroll.
                 */
                if (trackTable.getItems().contains(currentTrack)) {
                    trackTable.scrollTo(currentTrack);
                }

                setPauseButtonState();
                showSkipButton();
            }
        }

        @Override
        public void onPlaybackFinished() {
            hideSkipButton();
            setPlayButtonState();
            timeLabel.setText("0:00 / 0:00");
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
                setPauseButtonState();
                showSkipButton();
                return;
            }

            if (audioPlayerService.isPlaying()) {
                audioPlayerService.pause();
                setPlayButtonState();
            } else if (audioPlayerService.isPaused()) {
                audioPlayerService.resume();
                setPauseButtonState();
            } else {
                audioPlayerService.playTrack(tracciaSelezionata, getCurrentTrackList());
                setPauseButtonState();
                showSkipButton();
            }
        }

        // === SKIP ORA È TOTALMENTE SGANCIATO DALLA TABELLA ===
        @FXML
        public void onSkipClick() {
            if (audioPlayerService.getCurrentTrack() != null) {
                audioPlayerService.playNextTrack();
            }
        }

        @FXML
        public void onSkipPlaylistClick() {
            if (playlistAttuale == null) {
                AlertUtil.showError(
                        "Nessuna playlist attiva",
                        "Lo skip playlist è disponibile solo quando ti trovi dentro una playlist."
                );
                return;
            }

            if (libreria.getPlaylists().isEmpty()) {
                AlertUtil.showError(
                        "Nessuna playlist disponibile",
                        "Non ci sono playlist disponibili."
                );
                return;
            }

            PlaybackMode selectedMode = playbackModeComboBox.getValue();

            PlaylistNavigationStrategy strategy =
                    PlaylistNavigationStrategyFactory.create(selectedMode);

            Playlist prossimaPlaylist =
                    strategy.getNextPlaylist(libreria.getPlaylists(), playlistAttuale);

            if (prossimaPlaylist == null) {
                audioPlayerService.stop();
                hideSkipButton();
                setPlayButtonState();
                timeLabel.setText("0:00 / 0:00");

                AlertUtil.showInfo(
                        "Fine playlist",
                        "Non ci sono altre playlist da riprodurre in modalità sequenziale."
                );
                return;
            }

            playlistAttuale = prossimaPlaylist;

            playlistListView.getSelectionModel().select(prossimaPlaylist);
            currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
            trackTable.setItems(playlistAttuale.getTracks());

            showSkipPlaylistButton();
            clearTrackInputs();

            if (playlistAttuale.getTracks().isEmpty()) {
                audioPlayerService.stop();
                hideSkipButton();
                setPlayButtonState();
                timeLabel.setText("0:00 / 0:00");

                AlertUtil.showInfo(
                        "Playlist vuota",
                        "La playlist selezionata non contiene tracce da riprodurre."
                );
                return;
            }

            Track primaTraccia = playlistAttuale.getTracks().get(0);

            audioPlayerService.playTrack(primaTraccia, playlistAttuale.getTracks());
            setPauseButtonState();
            showSkipButton();
        }

        private void showSkipPlaylistButton() {
            skipPlaylistButton.setVisible(true);
            skipPlaylistButton.setManaged(true);
        }

        private void hideSkipPlaylistButton() {
            skipPlaylistButton.setVisible(false);
            skipPlaylistButton.setManaged(false);
        }

        @FXML
        public void onStopClick() {
            if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
                AlertUtil.showError("Audio già fermo", "Non c'è nessuna traccia in riproduzione o in pausa da fermare.");
                return;
            }

            audioPlayerService.stop();
            hideSkipButton();
            setPlayButtonState();

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
            hideSkipPlaylistButton();
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
                        titleInput.getText(),
                        authorInput.getText(),
                        lengthInput.getText(),
                        genreInput.getText(),
                        yearInput.getText(),
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
                        tracciaSelezionata,
                        titleInput.getText(),
                        authorInput.getText(),
                        lengthInput.getText(),
                        genreInput.getText(),
                        yearInput.getText(),
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
                AlertUtil.showError(
                        "Nessuna selezione",
                        "Seleziona la playlist che vuoi eliminare dalla barra laterale."
                );
                return;
            }

            libraryService.deletePlaylist(libreria, playlistSelezionata);

            if (playlistAttuale == playlistSelezionata) {
                onViewAllTracksClick();
            }
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
                hideSkipButton();
                setPlayButtonState();
            }

            if (playlistAttuale == null) {
                libraryService.deleteTrackGlobal(libreria, tracciaSelezionata);
            } else {
                playlistService.removeTrackFromPlaylist(playlistAttuale, tracciaSelezionata);
            }

            clearTrackInputs();
        }
    }