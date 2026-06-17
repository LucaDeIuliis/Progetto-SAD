package org.example.mediamusicplayer.controller;

import javafx.scene.control.TableRow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.SnapshotParameters;
import org.example.mediamusicplayer.persistence.DatabaseInitializer;
import org.example.mediamusicplayer.persistence.DatabaseManager;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import org.example.mediamusicplayer.service.command.CommandManager;
import org.example.mediamusicplayer.service.command.DeletePlaylistCommand;
import org.example.mediamusicplayer.service.command.DeleteTrackCommand;
import org.example.mediamusicplayer.service.command.AssignTrackCommand;
import org.example.mediamusicplayer.service.statistics.PlaybackStatisticsService;
import org.example.mediamusicplayer.service.playlistnavigation.PlaylistNavigationStrategy;
import org.example.mediamusicplayer.service.playlistnavigation.PlaylistNavigationStrategyFactory;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.model.TrackTag;
import org.example.mediamusicplayer.service.AudioPlayerService;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.util.AlertUtil;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.service.playback.PlaybackMode;
import org.example.mediamusicplayer.service.playback.PlaybackObserver;
import org.example.mediamusicplayer.repository.TrackRepository;
import org.example.mediamusicplayer.repository.PlaylistRepository;
import org.example.mediamusicplayer.repository.MusicLibraryRepository;
import java.time.Year;
import java.util.Optional;
import org.example.mediamusicplayer.service.persistence.PersistenceService;

public class MusicPlayerController implements PlaybackObserver {

    @FXML private ListView<Playlist> playlistListView;
    @FXML private TextField newPlaylistInput;
    @FXML private Label currentPlaylistLabel;
    @FXML private ComboBox<Playlist> playlistComboBox;
    @FXML private ComboBox<PlaybackMode> playbackModeComboBox;
    @FXML private ComboBox<String> autoPlaylistTypeComboBox;

    @FXML private Button playPauseButton;
    @FXML private Button skipButton;
    @FXML private Button skipPlaylistButton;
    @FXML private Label timeLabel;

    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> tagsColumn;
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
    @FXML private TextField autoPlaylistFilterInput;

    @FXML private CheckBox favCheck;
    @FXML private CheckBox explicitCheck;
    @FXML private CheckBox newReleaseCheck;

    // Inseriscila qui, fuori da qualsiasi metodo:
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private MusicLibrary libreria;
    private Playlist playlistAttuale;
    private Playlist playlistCorrente;
    private Playlist playlistInRiproduzione;
    private TrackService trackService;
    private PlaylistService playlistService;
    private MusicLibraryService libraryService;
    private AudioPlayerService audioPlayerService;
    private PlaybackStatisticsService playbackStatisticsService;
    private DatabaseManager databaseManager;
    private DatabaseInitializer databaseInitializer;
    private TrackRepository trackRepository;
    private PlaylistRepository playlistRepository;
    private MusicLibraryRepository musicLibraryRepository;
    private PersistenceService persistenceService;

    // === GESTORE COMANDI PER UNDO ===
    private CommandManager commandManager;

    @FXML
    public void initialize() {
        trackService = new TrackService();
        playlistService = new PlaylistService();
        libraryService = new MusicLibraryService();
        audioPlayerService = new AudioPlayerService();
        playbackStatisticsService = new PlaybackStatisticsService();

        databaseManager = new DatabaseManager();
        databaseInitializer = new DatabaseInitializer(databaseManager);
        databaseInitializer.initializeDatabase();

        trackRepository = new TrackRepository(databaseManager);
        playlistRepository = new PlaylistRepository(databaseManager);
        musicLibraryRepository = new MusicLibraryRepository(databaseManager, trackRepository, playlistRepository);
        persistenceService = new PersistenceService(musicLibraryRepository, trackRepository, playlistRepository);
        libreria = new MusicLibrary();
        commandManager = new CommandManager();

        audioPlayerService.addObserver(this);

        playbackModeComboBox.getItems().setAll(PlaybackMode.values());
        playbackModeComboBox.setValue(PlaybackMode.SEQUENTIAL);

        audioPlayerService.setPlaybackMode(PlaybackMode.SEQUENTIAL);

        playbackModeComboBox.valueProperty().addListener((obs, oldMode, newMode) -> {
            if (newMode != null) {
                audioPlayerService.setPlaybackMode(newMode);
            }
        });
        autoPlaylistTypeComboBox.getItems().setAll("Genere", "Anno");
        autoPlaylistTypeComboBox.setValue("Genere");

        if (tagsColumn != null) {
            tagsColumn.setCellValueFactory(new PropertyValueFactory<>("visualTags"));
        }
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLength"));

        playlistListView.setItems(libreria.getPlaylists());

        FilteredList<Playlist> normalPlaylistsOnly = new FilteredList<>(
                libreria.getPlaylists(),
                p -> !isSmartPlaylist(p) && !p.isGenerataAutomaticamente()
        );
        playlistComboBox.setItems(normalPlaylistsOnly);

        trackTable.setItems(libreria.getAllTracks());

        // =======================================================
        // MOTORE DRAG AND DROP ESTETICO + DOPPIO CLIC SULLA RIGA
        // =======================================================
        trackTable.setRowFactory(tv -> {
            TableRow<Track> row = new TableRow<>();

            // === IL FIX: Gestione doppio clic solo se la riga NON è vuota ===
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Track tracciaSelezionata = row.getItem();

                    audioPlayerService.playTrack(
                            tracciaSelezionata,
                            getCurrentTrackList()
                    );

                    playlistCorrente = playlistAttuale;
                    playlistInRiproduzione = playlistAttuale;

                    /*
                     * Se la traccia viene avviata dalla vista di una playlist,
                     * anche la playlist viene considerata riprodotta.
                     */
                    if (playlistAttuale != null) {
                        playbackStatisticsService.registerPlaylistPlayback(
                                playlistAttuale
                        );
                        saveLibraryAsync();
                    }

                    updateCurrentPlaylistLabel();
                    setPauseButtonState();
                    showSkipButton();
                }
            });
            // ================================================================

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);

                    // Crea un'immagine solida della riga esattamente com'è
                    SnapshotParameters sp = new SnapshotParameters();
                    db.setDragView(row.snapshot(sp, null));

                    // NESSUNA TRASPARENZA applicata alla riga originale

                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.MOVE);

                        // Linea guida blu e pulita per l'inserimento
                        row.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2 0 0 0;");
                        event.consume();
                    }
                }
            });

            row.setOnDragExited(event -> {
                row.setStyle(""); // Pulisce lo stile appena il mouse si sposta
                event.consume();
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);

                    Track draggedTrack = trackTable.getItems().remove(draggedIndex);

                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = trackTable.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    trackTable.getItems().add(dropIndex, draggedTrack);
                    event.setDropCompleted(true);
                    trackTable.getSelectionModel().select(dropIndex);

                    row.setStyle("");

                    commandManager.clearHistory();
                    saveLibraryAsync();
                    event.consume();
                }
            });

            return row;
        });
        // =======================================================

        // DOPPIO CLICK SULLA PLAYLIST PER RIPRODURRE
        playlistListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Playlist playlistSelezionata =
                        playlistListView.getSelectionModel().getSelectedItem();
                if (playlistSelezionata == null) {
                    AlertUtil.showError(
                            "Nessuna playlist selezionata",
                            "Seleziona una playlist da riprodurre."
                    );
                    return;
                }
                startPlaylistPlayback(playlistSelezionata);
            }
        });

        playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                playlistAttuale = nuova;
                if (!audioPlayerService.isPlaying()) {
                    currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
                }
                trackTable.setItems(playlistAttuale.getTracks());
                showSkipPlaylistButton();
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

                if (favCheck != null) favCheck.setSelected(nuova.getTags().contains(TrackTag.FAVOURITE));
                if (explicitCheck != null) explicitCheck.setSelected(nuova.getTags().contains(TrackTag.EXPLICIT));
                if (newReleaseCheck != null) newReleaseCheck.setSelected(nuova.getTags().contains(TrackTag.NEW_RELEASE));

                if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
                    timeLabel.setText("0:00 / " + nuova.getFormattedLength());
                }
            } else {
                if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
                    timeLabel.setText("0:00 / 0:00");
                }
            }
        });
        loadLibraryAsync();
    }

    // === GESTIONE UNDO (Delegata al CommandManager) ===
    @FXML
    public void onUndoClick() {
        if (commandManager.undoLastCommand()) {
            syncSmartPlaylists();
            refreshTableUI();
            saveLibraryAsync();
            AlertUtil.showInfo("Annullato", "L'ultima operazione è stata annullata con successo!");
        } else {
            AlertUtil.showInfo("Nessuna azione", "Non ci sono operazioni recenti da annullare.");
        }
    }

    private boolean isSmartPlaylist(Playlist p) {
        if(p == null) return false;
        return getTagFromPlaylistName(p.getName()) != null;
    }

    private TrackTag getTagFromPlaylistName(String name) {
        for (TrackTag tag : TrackTag.values()) {
            String nomeBase = switch (tag) {
                case FAVOURITE -> "I Miei Preferiti";
                case EXPLICIT -> "Brani Espliciti";
                case NEW_RELEASE -> "Nuove Uscite";
            };
            if (name.equals(nomeBase + " " + tag.getSymbol())) return tag;
        }
        return null;
    }

    private void syncSmartPlaylists() {
        for (TrackTag tag : TrackTag.values()) {
            String nomeBase = switch (tag) {
                case FAVOURITE -> "I Miei Preferiti";
                case EXPLICIT -> "Brani Espliciti";
                case NEW_RELEASE -> "Nuove Uscite";
            };
            String nomeCompleto = nomeBase + " " + tag.getSymbol();

            java.util.List<Track> tracceTaggate = libreria.getAllTracks().stream()
                    .filter(track -> track.getTags().contains(tag))
                    .toList();

            Playlist playlistEsistente = libreria.getPlaylists().stream()
                    .filter(p -> p.getName().equals(nomeCompleto))
                    .findFirst()
                    .orElse(null);

            if (playlistEsistente != null) {
                playlistEsistente.getTracks().clear();
                playlistEsistente.getTracks().addAll(tracceTaggate);

                if (playlistAttuale == playlistEsistente) {
                    trackTable.setItems(playlistEsistente.getTracks());
                    trackTable.refresh();
                }
            } else if (!tracceTaggate.isEmpty()) {
                try {
                    Playlist nuovaAuto = playlistService.createPlaylist(nomeCompleto, libreria);
                    nuovaAuto.getTracks().addAll(tracceTaggate);
                    libraryService.addPlaylist(libreria, nuovaAuto);
                } catch (PlaylistValidationException ignored) {}
            }
        }
        playlistListView.refresh();

        FilteredList<Playlist> normalPlaylistsOnly = new FilteredList<>(
                libreria.getPlaylists(),
                p -> !isSmartPlaylist(p) && !p.isGenerataAutomaticamente()
        );
        playlistComboBox.setItems(normalPlaylistsOnly);
    }

    private void refreshTableUI() {
        if (playlistAttuale != null) {
            trackTable.setItems(playlistAttuale.getTracks());
        } else {
            trackTable.setItems(libreria.getAllTracks());
        }
        trackTable.refresh();
        playlistListView.refresh();
    }

    private void clearTrackInputs() {
        titleInput.clear();
        authorInput.clear();
        lengthInput.clear();
        genreInput.clear();
        yearInput.clear();
        trackTable.getSelectionModel().clearSelection();

        if (favCheck != null) favCheck.setSelected(false);
        if (explicitCheck != null) explicitCheck.setSelected(false);
        if (newReleaseCheck != null) newReleaseCheck.setSelected(false);
    }

    private java.util.List<Track> getCurrentTrackList() {
        return trackTable.getItems();
    }

    private boolean startPlaylistPlayback(Playlist playlist) {
        if (playlist == null) {
            AlertUtil.showError(
                    "Playlist non disponibile",
                    "Non è stata specificata una playlist da riprodurre."
            );
            return false;
        }

        if (playlist.getTracks().isEmpty()) {
            AlertUtil.showInfo(
                    "Playlist vuota",
                    "La playlist selezionata non contiene tracce da riprodurre."
            );
            return false;
        }

        /*
         * La playlist viene considerata riprodotta nel momento
         * in cui ne viene effettivamente avviata la prima traccia.
         */
        playbackStatisticsService.registerPlaylistPlayback(playlist);
        saveLibraryAsync();

        playlistInRiproduzione = playlist;
        playlistCorrente = playlist;
        updateCurrentPlaylistLabel();
        Track primaTraccia = playlist.getTracks().get(0);
        audioPlayerService.playTrack(primaTraccia, playlist.getTracks());
        setPauseButtonState();
        showSkipButton();
        return true;
    }

    private void updateCurrentPlaylistLabel() {
        if (playlistInRiproduzione != null) {
            currentPlaylistLabel.setText(
                    "Stai ascoltando Playlist: " + playlistInRiproduzione.getName()
            );
        } else if (playlistAttuale != null) {
            currentPlaylistLabel.setText(
                    "Playlist selezionata: " + playlistAttuale.getName()
            );
        } else {
            currentPlaylistLabel.setText(
                    "Gestione Tracce: Tutte le canzoni"
            );
        }
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

    private void saveLibraryAsync() {
        if (persistenceService == null || libreria == null) {
            return;
        }

        persistenceService.saveLibraryAsync(libreria);
    }

    public void shutdown() {
        if (persistenceService != null) {
            persistenceService.shutdown();
        }
    }

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
            if (trackTable.getItems().contains(currentTrack)) {
                trackTable.scrollTo(currentTrack);
            }
            setPauseButtonState();
            showSkipButton();
        }
    }

    @Override
    public void onTrackHalfPlayed(Track track) {
        playbackStatisticsService.registerTrackPlayback(track);
        saveLibraryAsync();
    }
    @Override
    public void onPlaybackFinished() {
        hideSkipButton();
        setPlayButtonState();
        timeLabel.setText("0:00 / 0:00");
    }
    @FXML
    public void onShowStatisticsClick() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/mediamusicplayer/statistics-view.fxml")
            );

            Parent root = loader.load();

            StatisticsController statisticsController = loader.getController();

            Scene playerScene = trackTable.getScene();

            statisticsController.setData(
                    libreria,
                    playbackStatisticsService,
                    playerScene
            );

            Stage stage = (Stage) trackTable.getScene().getWindow();
            Scene statisticsScene = new Scene(root);

            stage.setScene(statisticsScene);
            stage.setTitle("Statistiche di ascolto");

        } catch (IOException e) {
            AlertUtil.showError(
                    "Errore apertura statistiche",
                    "Non è stato possibile aprire la schermata delle statistiche."
            );
        }
    }
    // === COMANDI AUDIO ===

    @FXML
    public void onPlayPauseClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        Track tracciaCorrente = audioPlayerService.getCurrentTrack();

        if (tracciaSelezionata == null && tracciaCorrente == null) {
            /*
             * Se è selezionata una playlist, PLAY deve avviare la playlist
             * dalla prima traccia e registrare una riproduzione.
             */
            if (playlistAttuale != null) {
                startPlaylistPlayback(playlistAttuale);
                return;
            }

            /*
             * Se siamo nella vista generale, senza playlist e senza traccia selezionata,
             * non sappiamo quale elemento riprodurre.
             */
            AlertUtil.showError(
                    "Nessuna traccia",
                    "Seleziona una traccia oppure una playlist da riprodurre."
            );
            return;
        }

        if (tracciaSelezionata == null) {
            tracciaSelezionata = tracciaCorrente;
        }

        if (audioPlayerService.isPlaying()) {
            audioPlayerService.pause();
            setPlayButtonState();
            return;
        }

        if (!tracciaSelezionata.equals(tracciaCorrente)) {
            audioPlayerService.playTrack(tracciaSelezionata, getCurrentTrackList());
            playlistCorrente = playlistAttuale;
            playlistInRiproduzione = playlistAttuale;

            updateCurrentPlaylistLabel();
            setPauseButtonState();
            showSkipButton();
            return;
        }

        if (audioPlayerService.isPaused()) {
            audioPlayerService.resume();
            setPauseButtonState();
        } else if (!audioPlayerService.isPlaying()) {
            audioPlayerService.playTrack(tracciaSelezionata, getCurrentTrackList());
            setPauseButtonState();
            showSkipButton();
        }
    }

    @FXML
    public void onSkipClick() {
        if (audioPlayerService.getCurrentTrack() != null) {
            audioPlayerService.playNextTrack();
        }
    }

    @FXML
    public void onSkipPlaylistClick() {
        boolean isPlaylistReallyPlaying = false;

        if (playlistCorrente != null && audioPlayerService.getCurrentTrack() != null) {
            if (playlistCorrente.getTracks().contains(audioPlayerService.getCurrentTrack())) {
                isPlaylistReallyPlaying = true;
            }
        }

        if (!isPlaylistReallyPlaying) {
            AlertUtil.showError(
                    "Azione non valida",
                    "Lo skip della playlist è disponibile solo mentre stai riproducendo un brano di una playlist."
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
                strategy.getNextPlaylist(libreria.getPlaylists(), playlistCorrente);

        if (prossimaPlaylist == null) {
            audioPlayerService.stop();
            playlistCorrente = null;
            playlistInRiproduzione = null;

            hideSkipButton();
            setPlayButtonState();
            timeLabel.setText("0:00 / 0:00");

            updateCurrentPlaylistLabel();

            AlertUtil.showInfo(
                    "Fine playlist",
                    "Non ci sono altre playlist da riprodurre."
            );
            return;
        }

        playlistAttuale = prossimaPlaylist;

        playlistListView.getSelectionModel().select(prossimaPlaylist);
        trackTable.setItems(prossimaPlaylist.getTracks());

        showSkipPlaylistButton();
        clearTrackInputs();

        startPlaylistPlayback(prossimaPlaylist);

        if (playlistCorrente.getTracks().isEmpty()) {
            audioPlayerService.stop();
            hideSkipButton();
            setPlayButtonState();
            timeLabel.setText("0:00 / 0:00");

            AlertUtil.showInfo(
                    "Playlist vuota",
                    "La playlist successiva '" + prossimaPlaylist.getName() + "' è vuota."
            );
            return;
        }

        Track primaTraccia = playlistCorrente.getTracks().get(0);

        audioPlayerService.playTrack(
                primaTraccia,
                playlistCorrente.getTracks()
        );
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

    private void loadLibraryAsync() {
        persistenceService.loadLibraryAsync(loadedLibrary -> {
                    libreria.getAllTracks().setAll(loadedLibrary.getAllTracks());
                    libreria.getPlaylists().setAll(loadedLibrary.getPlaylists());
                    refreshTableUI();
                },
                error -> {
                    error.printStackTrace();
                    AlertUtil.showError(
                            "Errore di caricamento",
                            "Impossibile caricare la libreria dal database."
                    );
                }
        );
    }

    @FXML
    public void onStopClick() {
        if (!audioPlayerService.isPlaying() && !audioPlayerService.isPaused()) {
            AlertUtil.showError("Audio già fermo", "Non c'è nessuna traccia in riproduzione o in pausa da fermare.");
            return;
        }

        audioPlayerService.stop();
        playlistCorrente = null;
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

            commandManager.clearHistory();
            saveLibraryAsync();
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

        if (isSmartPlaylist(playlistSelezionata)) {
            AlertUtil.showError("Azione non consentita", "Le Smart Playlist non possono essere rinominate.");
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
                // Non creiamo un Command per il rinomina, è un'operazione leggera,
                // puliamo lo stack per non corrompere gli indici.
                playlistService.renamePlaylist(playlistSelezionata, nuovoNome, libreria);

                playlistListView.refresh();

                FilteredList<Playlist> normalPlaylistsOnly = new FilteredList<>(
                        libreria.getPlaylists(),
                        p -> !isSmartPlaylist(p) && !p.isGenerataAutomaticamente()
                );
                playlistComboBox.setItems(null);
                playlistComboBox.setItems(normalPlaylistsOnly);

                if (playlistAttuale == playlistSelezionata) {
                    currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistSelezionata.getName());
                }
                commandManager.clearHistory();
                saveLibraryAsync();
            } catch (PlaylistValidationException e) {
                AlertUtil.showError(e.getHeader(), e.getMessage());
            }
        });
    }

    @FXML
    public void onAddTrackClick() {
        try {
            if (playlistAttuale != null && playlistAttuale.isGenerataAutomaticamente()) {
                String tipoFiltro = playlistAttuale.getTipoFiltro();
                String filtro = playlistAttuale.getFiltroAutomatico();
                if (tipoFiltro != null && filtro != null) {
                    boolean compatibile = true;
                    switch (tipoFiltro) {
                        case "Genere":
                            compatibile = genreInput.getText().equalsIgnoreCase(filtro);
                            break;
                        case "Anno":
                            compatibile = yearInput.getText().equals(filtro);
                            break;
                    }
                    if (!compatibile) {
                        AlertUtil.showError("Traccia non compatibile", "La traccia non rispetta il filtro della playlist automatica.");
                        return;
                    }
                }
            }

            Track nuovaTraccia = trackService.createTrack(
                    titleInput.getText(), authorInput.getText(), lengthInput.getText(), genreInput.getText(), yearInput.getText(), libreria
            );

            if (favCheck != null && favCheck.isSelected()) nuovaTraccia.addTag(TrackTag.FAVOURITE);
            if (explicitCheck != null && explicitCheck.isSelected()) nuovaTraccia.addTag(TrackTag.EXPLICIT);
            if (newReleaseCheck != null && newReleaseCheck.isSelected()) nuovaTraccia.addTag(TrackTag.NEW_RELEASE);

            if (playlistAttuale != null) {
                TrackTag tag = getTagFromPlaylistName(playlistAttuale.getName());
                if (tag != null) nuovaTraccia.addTag(tag);
            }

            libraryService.addTrackToLibrary(libreria, nuovaTraccia);
            if (playlistAttuale != null && !isSmartPlaylist(playlistAttuale)) {
                playlistService.addTrackToPlaylist(playlistAttuale, nuovaTraccia);
            }

            commandManager.clearHistory();

            syncSmartPlaylists();
            playlistService.syncTrackWithAutomaticPlaylists(nuovaTraccia, libreria);
            refreshTableUI();
            clearTrackInputs();

            saveLibraryAsync();

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
            trackService.updateTrack(tracciaSelezionata, titleInput.getText(), authorInput.getText(), lengthInput.getText(), genreInput.getText(), yearInput.getText(), libreria);
            tracciaSelezionata.getTags().clear();
            if (favCheck != null && favCheck.isSelected()) tracciaSelezionata.addTag(TrackTag.FAVOURITE);
            if (explicitCheck != null && explicitCheck.isSelected()) tracciaSelezionata.addTag(TrackTag.EXPLICIT);
            if (newReleaseCheck != null && newReleaseCheck.isSelected()) tracciaSelezionata.addTag(TrackTag.NEW_RELEASE);

            commandManager.clearHistory();

            syncSmartPlaylists();
            playlistService.syncTrackWithAutomaticPlaylists(tracciaSelezionata, libreria);
            refreshTableUI();
            clearTrackInputs();

            saveLibraryAsync();
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

        if (isSmartPlaylist(playlistScelta)) {
            AlertUtil.showError("Azione non consentita", "Questa è una Smart Playlist automatica. Usa i tag per aggiungere brani.");
            return;
        }

        // DELEGAZIONE AL COMMAND MANAGER
        try {
            // Tentativo preventivo di validazione
            if (playlistScelta.getTracks().contains(tracciaSelezionata)) {
                // ---> IL FIX È QUI: Passiamo due parametri (Header, Messaggio) <---
                throw new PlaylistValidationException("Traccia Duplicata", "La traccia selezionata è già presente in questa playlist.");
            }
            AssignTrackCommand cmd = new AssignTrackCommand(playlistScelta, tracciaSelezionata, playlistService);
            commandManager.executeCommand(cmd);
            saveLibraryAsync();

            AlertUtil.showInfo("Fatto!", "Traccia aggiunta a " + playlistScelta.getName());
            if (playlistAttuale == playlistScelta) refreshTableUI();

        } catch (PlaylistValidationException e) {
            // Ora anche il catch usa correttamente i due metodi
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }
    @FXML
    public void onDeletePlaylistClick() {
        Playlist playlistSelezionata =
                playlistListView.getSelectionModel().getSelectedItem();
        if (playlistSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna selezione",
                    "Seleziona la playlist che vuoi eliminare dalla barra laterale."
            );
            return;
        }
        // Blocca solo le Smart Playlist basate sui tag
        if (isSmartPlaylist(playlistSelezionata)) {
            AlertUtil.showError(
                    "Azione non consentita",
                    "Le playlist automatiche di sistema non possono essere eliminate."
            );
            return;
        }
        String playlistId = playlistSelezionata.getId();
        DeletePlaylistCommand cmd =
                new DeletePlaylistCommand(
                        playlistSelezionata,
                        libreria,
                        libraryService
                );
        commandManager.executeCommand(cmd);
        persistenceService.deletePlaylistAsync(playlistId);
        if (playlistAttuale == playlistSelezionata) {
            onViewAllTracksClick();
        }
        if (playlistSelezionata == playlistCorrente) {
            playlistCorrente = null;
        }
        playlistListView.refresh();
        playlistComboBox.getItems().remove(playlistSelezionata);
        saveLibraryAsync();
        AlertUtil.showInfo(
                "Playlist eliminata",
                "La playlist è stata eliminata correttamente."
        );
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

        TrackTag potenzialeTag = null;
        if (playlistAttuale != null) {
            potenzialeTag = getTagFromPlaylistName(playlistAttuale.getName());
        }

        // DELEGAZIONE AL COMMAND MANAGER
        String trackId = tracciaSelezionata.getId();
        DeleteTrackCommand cmd = new DeleteTrackCommand(tracciaSelezionata, playlistAttuale, potenzialeTag, libreria, libraryService, playlistService);
        commandManager.executeCommand(cmd);
        persistenceService.deleteTrackAsync(trackId);

        syncSmartPlaylists();
        refreshTableUI();
        clearTrackInputs();
    }

    @FXML
    public void onCreateAutomaticPlaylistClick() {

        String filtro = autoPlaylistFilterInput.getText().trim();
        String tipo = autoPlaylistTypeComboBox.getValue();

        if (filtro.isEmpty()) {
            AlertUtil.showError("Filtro mancante", "Inserisci un genere o un anno.");
            return;
        }

        java.util.List<Track> tracceTrovate = new java.util.ArrayList<>();

        for (Track track : libreria.getAllTracks()) {
            if (tipo.equals("Genere")) {
                if (track.getGenre().equalsIgnoreCase(filtro)) tracceTrovate.add(track);
            } else if (tipo.equals("Anno")) {
                if (String.valueOf(track.getYear().getValue()).equals(filtro)) tracceTrovate.add(track);
            }
        }

        if (tracceTrovate.isEmpty()) {
            AlertUtil.showInfo("Nessun risultato", "Non sono state trovate tracce compatibili.");
            return;
        }

        try {
            String nomePlaylist = "Auto - " + tipo + " " + filtro;
            Playlist nuovaPlaylist = playlistService.createPlaylist(nomePlaylist, libreria);
            nuovaPlaylist.setGenerataAutomaticamente(true);
            nuovaPlaylist.setTipoFiltro(tipo);
            nuovaPlaylist.setFiltroAutomatico(filtro);
            libraryService.addPlaylist(libreria, nuovaPlaylist);

            for(Track t : libreria.getAllTracks()) {
                if(playlistService.trackRispettaFiltro(nuovaPlaylist, t)) {
                    nuovaPlaylist.addTrack(t);
                }
            }

            playlistListView.getSelectionModel().select(nuovaPlaylist);
            AlertUtil.showInfo("Playlist creata", "Aggiunte " + tracceTrovate.size() + " tracce alla playlist.");

            FilteredList<Playlist> normalPlaylistsOnly = new FilteredList<>(
                    libreria.getPlaylists(),
                    p -> !isSmartPlaylist(p) && !p.isGenerataAutomaticamente()
            );
            playlistComboBox.setItems(normalPlaylistsOnly);

            commandManager.clearHistory();
            saveLibraryAsync();

        } catch (PlaylistValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }
}
