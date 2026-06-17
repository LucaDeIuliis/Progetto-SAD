package org.example.mediamusicplayer.controller;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.model.TrackTag;
import org.example.mediamusicplayer.service.AudioPlayerService;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.service.command.AssignTrackCommand;
import org.example.mediamusicplayer.service.command.CommandManager;
import org.example.mediamusicplayer.service.command.DeletePlaylistCommand;
import org.example.mediamusicplayer.service.command.DeleteTrackCommand;
import org.example.mediamusicplayer.service.persistence.PersistenceService;
import org.example.mediamusicplayer.service.playback.PlaybackMode;
import org.example.mediamusicplayer.service.playback.PlaybackObserver;
import org.example.mediamusicplayer.service.statistics.PlaybackStatisticsService;
import org.example.mediamusicplayer.util.AlertUtil;
import org.example.mediamusicplayer.service.playlistnavigation.PlaylistNavigationService;

import java.io.IOException;
import java.time.Year;
import java.util.Optional;

public class MusicPlayerController implements PlaybackObserver {

    @FXML
    private ListView<Playlist> playlistListView;

    @FXML
    private TextField newPlaylistInput;

    @FXML
    private Label currentPlaylistLabel;

    @FXML
    private ComboBox<Playlist> playlistComboBox;

    @FXML
    private ComboBox<PlaybackMode> playbackModeComboBox;

    @FXML
    private ComboBox<String> autoPlaylistTypeComboBox;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button skipButton;

    @FXML
    private Button skipPlaylistButton;

    @FXML
    private Label timeLabel;

    @FXML
    private TableView<Track> trackTable;

    @FXML
    private TableColumn<Track, String> tagsColumn;

    @FXML
    private TableColumn<Track, String> titleColumn;

    @FXML
    private TableColumn<Track, String> authorColumn;

    @FXML
    private TableColumn<Track, String> lengthColumn;

    @FXML
    private TableColumn<Track, String> genreColumn;

    @FXML
    private TableColumn<Track, Year> yearColumn;

    @FXML
    private TextField titleInput;

    @FXML
    private TextField authorInput;

    @FXML
    private TextField lengthInput;

    @FXML
    private TextField genreInput;

    @FXML
    private TextField yearInput;

    @FXML
    private TextField autoPlaylistFilterInput;

    @FXML
    private CheckBox favCheck;

    @FXML
    private CheckBox explicitCheck;

    @FXML
    private CheckBox newReleaseCheck;

    private static final DataFormat SERIALIZED_MIME_TYPE =
            new DataFormat("application/x-java-serialized-object");

    private final MusicLibrary libreria;
    private Playlist playlistAttuale;
    private Playlist playlistCorrente;
    private Playlist playlistInRiproduzione;
    private final PlaylistNavigationService playlistNavigationService;
    private final TrackService trackService;
    private final PlaylistService playlistService;
    private final MusicLibraryService libraryService;
    private final AudioPlayerService audioPlayerService;
    private final PlaybackStatisticsService playbackStatisticsService;
    private final PersistenceService persistenceService;

    private final CommandManager commandManager;

    public MusicPlayerController(
            MusicLibrary libreria,
            CommandManager commandManager,
            TrackService trackService,
            PlaylistService playlistService,
            MusicLibraryService libraryService,
            AudioPlayerService audioPlayerService,
            PlaybackStatisticsService playbackStatisticsService,
            PlaylistNavigationService playlistNavigationService,
            PersistenceService persistenceService
    ) {
        this.libreria = libreria;
        this.commandManager = commandManager;
        this.trackService = trackService;
        this.playlistService = playlistService;
        this.libraryService = libraryService;
        this.audioPlayerService = audioPlayerService;
        this.playbackStatisticsService = playbackStatisticsService;
        this.playlistNavigationService = playlistNavigationService;
        this.persistenceService = persistenceService;
    }

    @FXML
    public void initialize() {
        audioPlayerService.addObserver(this);
        playbackModeComboBox.getItems().setAll(PlaybackMode.values());
        playbackModeComboBox.setValue(PlaybackMode.SEQUENTIAL);
        audioPlayerService.setPlaybackMode(PlaybackMode.SEQUENTIAL);

        playbackModeComboBox.valueProperty().addListener(
                (observable, oldMode, newMode) -> {
                    if (newMode != null) {
                        audioPlayerService.setPlaybackMode(newMode);
                    }
                }
        );

        autoPlaylistTypeComboBox.getItems().setAll("Genere", "Anno");
        autoPlaylistTypeComboBox.setValue("Genere");

        if (tagsColumn != null) {
            tagsColumn.setCellValueFactory(
                    new PropertyValueFactory<>("visualTags")
            );
        }

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title")
        );
        authorColumn.setCellValueFactory(
                new PropertyValueFactory<>("author")
        );
        genreColumn.setCellValueFactory(
                new PropertyValueFactory<>("genre")
        );
        yearColumn.setCellValueFactory(
                new PropertyValueFactory<>("year")
        );
        lengthColumn.setCellValueFactory(
                new PropertyValueFactory<>("formattedLength")
        );

        playlistListView.setItems(libreria.getPlaylists());

        refreshAssignablePlaylists();
        trackTable.setItems(libreria.getAllTracks());

        trackTable.setRowFactory(tableView -> {
            TableRow<Track> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Track tracciaSelezionata = row.getItem();

                    audioPlayerService.playTrack(
                            tracciaSelezionata,
                            getCurrentTrackList()
                    );

                    playlistCorrente = playlistAttuale;
                    playlistInRiproduzione = playlistAttuale;

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

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    int index = row.getIndex();

                    Dragboard dragboard =
                            row.startDragAndDrop(TransferMode.MOVE);

                    SnapshotParameters snapshotParameters =
                            new SnapshotParameters();

                    dragboard.setDragView(
                            row.snapshot(snapshotParameters, null)
                    );

                    ClipboardContent content = new ClipboardContent();
                    content.put(SERIALIZED_MIME_TYPE, index);
                    dragboard.setContent(content);

                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard dragboard = event.getDragboard();

                if (dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex =
                            (Integer) dragboard.getContent(
                                    SERIALIZED_MIME_TYPE
                            );

                    if (row.getIndex() != draggedIndex) {
                        event.acceptTransferModes(TransferMode.MOVE);

                        row.setStyle(
                                "-fx-border-color: #2196F3; "
                                        + "-fx-border-width: 2 0 0 0;"
                        );

                        event.consume();
                    }
                }
            });

            row.setOnDragExited(event -> {
                row.setStyle("");
                event.consume();
            });

            row.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();

                if (dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex =
                            (Integer) dragboard.getContent(
                                    SERIALIZED_MIME_TYPE
                            );

                    int dropIndex;

                    if (row.isEmpty()) {
                        dropIndex = trackTable.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    try {
                        playlistService.moveTrack(
                                trackTable.getItems(),
                                draggedIndex,
                                dropIndex
                        );

                        event.setDropCompleted(true);
                        trackTable.getSelectionModel().select(dropIndex);

                        commandManager.clearHistory();
                        saveLibraryAsync();

                    } catch (PlaylistValidationException e) {
                        event.setDropCompleted(false);

                        AlertUtil.showError(
                                e.getHeader(),
                                e.getMessage()
                        );
                    }

                    row.setStyle("");
                    event.consume();
                }
            });

            return row;
        });

        playlistListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Playlist playlistSelezionata =
                        playlistListView
                                .getSelectionModel()
                                .getSelectedItem();

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

        playlistListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, vecchia, nuova) -> {
                    if (nuova != null) {
                        playlistAttuale = nuova;

                        if (!audioPlayerService.isPlaying()) {
                            currentPlaylistLabel.setText(
                                    "Stai ascoltando Playlist: "
                                            + playlistAttuale.getName()
                            );
                        }

                        trackTable.setItems(
                                playlistAttuale.getTracks()
                        );

                        showSkipPlaylistButton();
                        clearTrackInputs();
                    }
                });

        trackTable
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, vecchia, nuova) -> {
                    if (nuova != null) {
                        titleInput.setText(nuova.getTitle());
                        authorInput.setText(nuova.getAuthor());
                        genreInput.setText(nuova.getGenre());

                        yearInput.setText(
                                String.valueOf(
                                        nuova.getYear().getValue()
                                )
                        );

                        long totalSeconds =
                                nuova.getLength().getSeconds();

                        lengthInput.setText(
                                String.format(
                                        "%d:%02d",
                                        totalSeconds / 60,
                                        totalSeconds % 60
                                )
                        );

                        if (favCheck != null) {
                            favCheck.setSelected(
                                    nuova.getTags().contains(
                                            TrackTag.FAVOURITE
                                    )
                            );
                        }

                        if (explicitCheck != null) {
                            explicitCheck.setSelected(
                                    nuova.getTags().contains(
                                            TrackTag.EXPLICIT
                                    )
                            );
                        }

                        if (newReleaseCheck != null) {
                            newReleaseCheck.setSelected(
                                    nuova.getTags().contains(
                                            TrackTag.NEW_RELEASE
                                    )
                            );
                        }

                        if (!audioPlayerService.isPlaying()
                                && !audioPlayerService.isPaused()) {
                            timeLabel.setText(
                                    "0:00 / "
                                            + nuova.getFormattedLength()
                            );
                        }
                    } else {
                        if (!audioPlayerService.isPlaying()
                                && !audioPlayerService.isPaused()) {
                            timeLabel.setText("0:00 / 0:00");
                        }
                    }
                });

        loadLibraryAsync();
    }

    @FXML
    public void onUndoClick() {
        if (commandManager.undoLastCommand()) {
            syncSmartPlaylists();
            refreshTableUI();
            saveLibraryAsync();

            AlertUtil.showInfo(
                    "Annullato",
                    "L'ultima operazione è stata annullata con successo!"
            );
        } else {
            AlertUtil.showInfo(
                    "Nessuna azione",
                    "Non ci sono operazioni recenti da annullare."
            );
        }
    }

    private void syncSmartPlaylists() {
        playlistService.syncSmartPlaylists(
                libreria,
                libraryService
        );

        playlistListView.refresh();

        refreshAssignablePlaylists();

        if (playlistAttuale != null
                && !libreria.getPlaylists().contains(playlistAttuale)) {
            playlistAttuale = null;
            trackTable.setItems(libreria.getAllTracks());
        }

        trackTable.refresh();
    }

    private void refreshTableUI() {
        if (playlistAttuale != null) {
            trackTable.setItems(
                    playlistAttuale.getTracks()
            );
        } else {
            trackTable.setItems(
                    libreria.getAllTracks()
            );
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

        trackTable
                .getSelectionModel()
                .clearSelection();

        if (favCheck != null) {
            favCheck.setSelected(false);
        }

        if (explicitCheck != null) {
            explicitCheck.setSelected(false);
        }

        if (newReleaseCheck != null) {
            newReleaseCheck.setSelected(false);
        }
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

        playbackStatisticsService.registerPlaylistPlayback(
                playlist
        );

        saveLibraryAsync();

        playlistInRiproduzione = playlist;
        playlistCorrente = playlist;

        updateCurrentPlaylistLabel();

        Track primaTraccia =
                playlist.getTracks().get(0);

        audioPlayerService.playTrack(
                primaTraccia,
                playlist.getTracks()
        );

        setPauseButtonState();
        showSkipButton();

        return true;
    }

    private void updateCurrentPlaylistLabel() {
        if (playlistInRiproduzione != null) {
            currentPlaylistLabel.setText(
                    "Stai ascoltando Playlist: "
                            + playlistInRiproduzione.getName()
            );
        } else if (playlistAttuale != null) {
            currentPlaylistLabel.setText(
                    "Playlist selezionata: "
                            + playlistAttuale.getName()
            );
        } else {
            currentPlaylistLabel.setText(
                    "Gestione Tracce: Tutte le canzoni"
            );
        }
    }

    private void setPlayButtonState() {
        playPauseButton.setText("▶ PLAY");
        playPauseButton.setStyle(
                "-fx-background-color: #4CAF50; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 14px;"
        );
    }

    private void setPauseButtonState() {
        playPauseButton.setText("⏸ PAUSA");
        playPauseButton.setStyle(
                "-fx-background-color: #FFC107; "
                        + "-fx-text-fill: black; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 14px;"
        );
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
    public void onTimeUpdate(
            String currentTime,
            Track currentTrack
    ) {
        if (currentTrack != null) {
            timeLabel.setText(
                    currentTrack.getTitle()
                            + " - "
                            + currentTime
                            + " / "
                            + currentTrack.getFormattedLength()
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
        playbackStatisticsService.registerTrackPlayback(
                track
        );
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
            Scene playerScene = trackTable.getScene();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/org/example/mediamusicplayer/"
                                    + "statistics-view.fxml"
                    )
            );

            loader.setControllerFactory(controllerClass -> {
                if (controllerClass == StatisticsController.class) {
                    return new StatisticsController(
                            libreria,
                            playbackStatisticsService,
                            playerScene
                    );
                }

                try {
                    return controllerClass
                            .getDeclaredConstructor()
                            .newInstance();

                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException(
                            "Impossibile creare il controller: "
                                    + controllerClass.getName(),
                            e
                    );
                }
            });

            Parent root = loader.load();

            Stage stage =
                    (Stage) playerScene.getWindow();

            Scene statisticsScene = new Scene(root);

            stage.setScene(statisticsScene);
            stage.setTitle("Statistiche di ascolto");

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();

            AlertUtil.showError(
                    "Errore apertura statistiche",
                    "Non è stato possibile aprire la schermata "
                            + "delle statistiche."
            );
        }
    }

    @FXML
    public void onPlayPauseClick() {
        Track tracciaSelezionata =
                trackTable
                        .getSelectionModel()
                        .getSelectedItem();

        Track tracciaCorrente =
                audioPlayerService.getCurrentTrack();

        if (tracciaSelezionata == null
                && tracciaCorrente == null) {

            if (playlistAttuale != null) {
                startPlaylistPlayback(playlistAttuale);
                return;
            }

            AlertUtil.showError(
                    "Nessuna traccia",
                    "Seleziona una traccia oppure una playlist "
                            + "da riprodurre."
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
            audioPlayerService.playTrack(
                    tracciaSelezionata,
                    getCurrentTrackList()
            );

            playlistCorrente = playlistAttuale;
            playlistInRiproduzione = playlistAttuale;

            if (playlistAttuale != null) {
                playbackStatisticsService.registerPlaylistPlayback(
                        playlistAttuale
                );

                saveLibraryAsync();
            }

            updateCurrentPlaylistLabel();
            setPauseButtonState();
            showSkipButton();

            return;
        }

        if (audioPlayerService.isPaused()) {
            audioPlayerService.resume();
            setPauseButtonState();

        } else if (!audioPlayerService.isPlaying()) {
            audioPlayerService.playTrack(
                    tracciaSelezionata,
                    getCurrentTrackList()
            );

            playlistCorrente = playlistAttuale;
            playlistInRiproduzione = playlistAttuale;

            if (playlistAttuale != null) {
                playbackStatisticsService.registerPlaylistPlayback(
                        playlistAttuale
                );

                System.out.println(
                        "Nuovo contatore playlist: "
                                + playlistAttuale.getPlayCount()
                );

                saveLibraryAsync();
            }

            updateCurrentPlaylistLabel();
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

        Track currentTrack =
                audioPlayerService.getCurrentTrack();

        if (playlistCorrente != null
                && currentTrack != null
                && playlistCorrente
                .getTracks()
                .contains(currentTrack)) {
            isPlaylistReallyPlaying = true;
        }

        if (!isPlaylistReallyPlaying) {
            AlertUtil.showError(
                    "Azione non valida",
                    "Lo skip della playlist è disponibile solo "
                            + "mentre stai riproducendo un brano "
                            + "di una playlist."
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

        PlaybackMode selectedMode =
                playbackModeComboBox.getValue();

        Playlist prossimaPlaylist =
                playlistNavigationService.getNextPlaylist(
                        libreria.getPlaylists(),
                        playlistCorrente,
                        selectedMode
                );

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

        playlistListView
                .getSelectionModel()
                .select(prossimaPlaylist);

        trackTable.setItems(
                prossimaPlaylist.getTracks()
        );

        showSkipPlaylistButton();
        clearTrackInputs();

        if (!startPlaylistPlayback(prossimaPlaylist)) {
            return;
        }
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
        persistenceService.loadLibraryAsync(
                loadedLibrary -> {
                    libreria.getAllTracks().setAll(
                            loadedLibrary.getAllTracks()
                    );

                    libreria.getPlaylists().setAll(
                            loadedLibrary.getPlaylists()
                    );

                    syncSmartPlaylists();
                    refreshTableUI();
                    saveLibraryAsync();
                },
                error -> {
                    error.printStackTrace();

                    AlertUtil.showError(
                            "Errore di caricamento",
                            "Impossibile caricare la libreria "
                                    + "dal database."
                    );
                }
        );
    }

    @FXML
    public void onStopClick() {
        if (!audioPlayerService.isPlaying()
                && !audioPlayerService.isPaused()) {
            AlertUtil.showError(
                    "Audio già fermo",
                    "Non c'è nessuna traccia in riproduzione "
                            + "o in pausa da fermare."
            );
            return;
        }

        audioPlayerService.stop();
        playlistCorrente = null;
        playlistInRiproduzione = null;

        hideSkipButton();
        setPlayButtonState();

        Track tracciaSelezionata =
                trackTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (tracciaSelezionata != null) {
            timeLabel.setText(
                    "0:00 / "
                            + tracciaSelezionata
                            .getFormattedLength()
            );
        } else {
            timeLabel.setText("0:00 / 0:00");
        }
    }

    @FXML
    public void onViewAllTracksClick() {
        playlistAttuale = null;

        playlistListView
                .getSelectionModel()
                .clearSelection();

        trackTable.setItems(
                libreria.getAllTracks()
        );

        currentPlaylistLabel.setText(
                "Gestione Tracce: Tutte le canzoni"
        );

        hideSkipPlaylistButton();
        clearTrackInputs();
    }

    @FXML
    public void onAddPlaylistClick() {
        try {
            Playlist nuovaPlaylist =
                    playlistService.createPlaylist(
                            newPlaylistInput.getText(),
                            libreria
                    );

            libraryService.addPlaylist(
                    libreria,
                    nuovaPlaylist
            );

            newPlaylistInput.clear();

            playlistListView
                    .getSelectionModel()
                    .select(nuovaPlaylist);

            commandManager.clearHistory();
            saveLibraryAsync();
        } catch (PlaylistValidationException e) {
            AlertUtil.showError(
                    e.getHeader(),
                    e.getMessage()
            );
        }
    }

    @FXML
    public void onRenamePlaylistClick() {
        Playlist playlistSelezionata =
                playlistListView
                        .getSelectionModel()
                        .getSelectedItem();

        if (playlistSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna selezione",
                    "Seleziona la playlist da rinominare."
            );
            return;
        }

        if (playlistService.isSmartPlaylist(
                playlistSelezionata
        )) {
            AlertUtil.showError(
                    "Azione non consentita",
                    "Le Smart Playlist non possono essere rinominate."
            );
            return;
        }

        Optional<String> result = AlertUtil.askInput(
                "Rinomina Playlist",
                "Stai modificando: "
                        + playlistSelezionata.getName(),
                "Inserisci il nuovo nome:",
                playlistSelezionata.getName()
        );

        result.ifPresent(nuovoNome -> {
            try {
                playlistService.renamePlaylist(
                        playlistSelezionata,
                        nuovoNome,
                        libreria
                );

                playlistListView.refresh();

                FilteredList<Playlist> normalPlaylistsOnly =
                        new FilteredList<>(
                                libreria.getPlaylists(),
                                playlist ->
                                        !playlistService
                                                .isSmartPlaylist(
                                                        playlist
                                                )
                                                && !playlist
                                                .isGenerataAutomaticamente()
                        );

                playlistComboBox.setItems(
                        normalPlaylistsOnly
                );

                if (playlistAttuale
                        == playlistSelezionata) {
                    currentPlaylistLabel.setText(
                            "Stai ascoltando Playlist: "
                                    + playlistSelezionata
                                    .getName()
                    );
                }

                commandManager.clearHistory();
                saveLibraryAsync();
            } catch (PlaylistValidationException e) {
                AlertUtil.showError(
                        e.getHeader(),
                        e.getMessage()
                );
            }
        });
    }

    @FXML
    public void onAddTrackClick() {
        try {
            playlistService.validateTrackDataForAutomaticPlaylist(
                    playlistAttuale,
                    genreInput.getText(),
                    yearInput.getText()
            );

            Track nuovaTraccia =
                    trackService.createTrack(
                            titleInput.getText(),
                            authorInput.getText(),
                            lengthInput.getText(),
                            genreInput.getText(),
                            yearInput.getText(),
                            libreria
                    );

            trackService.updateTags(
                    nuovaTraccia,
                    favCheck != null && favCheck.isSelected(),
                    explicitCheck != null && explicitCheck.isSelected(),
                    newReleaseCheck != null && newReleaseCheck.isSelected()
            );

            libraryService.addTrackToLibrary(libreria, nuovaTraccia);

            if (playlistAttuale != null
                    && !playlistService.isSmartPlaylist(playlistAttuale)) {
                playlistService.addTrackToPlaylist(
                        playlistAttuale,
                        nuovaTraccia
                );
            }

            commandManager.clearHistory();

            syncSmartPlaylists();

            playlistService.syncTrackWithAutomaticPlaylists(
                    nuovaTraccia,
                    libreria
            );

            refreshTableUI();
            clearTrackInputs();
            saveLibraryAsync();

        } catch (TrackValidationException e) {
            AlertUtil.showError(
                    e.getHeader(),
                    e.getMessage()
            );
        } catch (PlaylistValidationException e) {
            AlertUtil.showError(
                    e.getHeader(),
                    e.getMessage()
            );
        }
    }

    @FXML
    public void onUpdateTrackClick() {
        Track tracciaSelezionata =
                trackTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (tracciaSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna selezione",
                    "Seleziona una traccia dalla tabella "
                            + "per modificarla."
            );
            return;
        }

        try {
            playlistService.validateTrackDataForAutomaticPlaylist(
                    playlistAttuale,
                    genreInput.getText(),
                    yearInput.getText()
            );

            trackService.updateTrack(
                    tracciaSelezionata,
                    titleInput.getText(),
                    authorInput.getText(),
                    lengthInput.getText(),
                    genreInput.getText(),
                    yearInput.getText(),
                    libreria
            );

            trackService.updateTags(
                    tracciaSelezionata,
                    favCheck != null && favCheck.isSelected(),
                    explicitCheck != null && explicitCheck.isSelected(),
                    newReleaseCheck != null && newReleaseCheck.isSelected()
            );

            commandManager.clearHistory();

            syncSmartPlaylists();

            playlistService
                    .syncTrackWithAutomaticPlaylists(
                            tracciaSelezionata,
                            libreria
                    );

            refreshTableUI();
            clearTrackInputs();
            saveLibraryAsync();
        } catch (TrackValidationException e) {
            AlertUtil.showError(
                    e.getHeader(),
                    e.getMessage()
            );
        } catch (PlaylistValidationException e) {
            AlertUtil.showError(
                    e.getHeader(),
                    e.getMessage()
            );
        }
    }

    @FXML
    public void onAssignToPlaylistClick() {
        Track tracciaSelezionata =
                trackTable
                        .getSelectionModel()
                        .getSelectedItem();

        Playlist playlistScelta =
                playlistComboBox.getValue();

        if (tracciaSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna Traccia",
                    "Seleziona prima una traccia."
            );
            return;
        }

        if (playlistScelta == null) {
            AlertUtil.showError(
                    "Nessuna Playlist",
                    "Seleziona una playlist."
            );
            return;
        }

        if (playlistService.isSmartPlaylist(
                playlistScelta
        )) {
            AlertUtil.showError(
                    "Azione non consentita",
                    "Questa è una Smart Playlist automatica. "
                            + "Usa i tag per aggiungere brani."
            );
            return;
        }

        try {
            AssignTrackCommand command =
                    new AssignTrackCommand(
                            playlistScelta,
                            tracciaSelezionata,
                            playlistService
                    );

            commandManager.executeCommand(command);
            saveLibraryAsync();

            AlertUtil.showInfo(
                    "Fatto!",
                    "Traccia aggiunta a " + playlistScelta.getName()
            );

            if (playlistAttuale == playlistScelta) {
                refreshTableUI();
            }

        } catch (PlaylistValidationException e) {
            AlertUtil.showError(
                    e.getHeader(),
                    e.getMessage()
            );
        }
    }

    @FXML
    public void onDeletePlaylistClick() {
        Playlist playlistSelezionata =
                playlistListView
                        .getSelectionModel()
                        .getSelectedItem();

        if (playlistSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna selezione",
                    "Seleziona la playlist che vuoi eliminare "
                            + "dalla barra laterale."
            );
            return;
        }

        if (playlistService.isSmartPlaylist(
                playlistSelezionata
        )) {
            AlertUtil.showError(
                    "Azione non consentita",
                    "Le playlist automatiche di sistema "
                            + "non possono essere eliminate."
            );
            return;
        }

        String playlistId =
                playlistSelezionata.getId();

        DeletePlaylistCommand command =
                new DeletePlaylistCommand(
                        playlistSelezionata,
                        libreria,
                        libraryService
                );

        commandManager.executeCommand(command);

        persistenceService.deletePlaylistAsync(
                playlistId
        );

        if (playlistAttuale == playlistSelezionata) {
            onViewAllTracksClick();
        }

        if (playlistSelezionata == playlistCorrente) {
            playlistCorrente = null;
        }

        if (playlistSelezionata
                == playlistInRiproduzione) {
            playlistInRiproduzione = null;
        }

        playlistListView.refresh();
        saveLibraryAsync();
        AlertUtil.showInfo(
                "Playlist eliminata",
                "La playlist è stata eliminata correttamente."
        );
    }

    @FXML
    public void onDeleteTrackClick() {
        Track tracciaSelezionata =
                trackTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (tracciaSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna selezione",
                    "Clicca su una traccia prima di eliminarla."
            );
            return;
        }

        if (tracciaSelezionata.equals(
                audioPlayerService.getCurrentTrack()
        )) {
            audioPlayerService.stop();
            hideSkipButton();
            setPlayButtonState();
        }

        TrackTag potenzialeTag =
                playlistService.getTagFromPlaylist(
                        playlistAttuale
                );

        String trackId =
                tracciaSelezionata.getId();

        DeleteTrackCommand command =
                new DeleteTrackCommand(
                        tracciaSelezionata,
                        playlistAttuale,
                        potenzialeTag,
                        libreria,
                        libraryService,
                        playlistService
                );

        commandManager.executeCommand(command);

        persistenceService.deleteTrackAsync(
                trackId
        );

        syncSmartPlaylists();
        refreshTableUI();
        clearTrackInputs();
    }

    @FXML
    public void onCreateAutomaticPlaylistClick() {
        String filtro = autoPlaylistFilterInput.getText();
        String tipo = autoPlaylistTypeComboBox.getValue();

        try {
            Playlist nuovaPlaylist =
                    playlistService.createAutomaticPlaylist(
                            tipo,
                            filtro,
                            libreria,
                            libraryService
                    );

            playlistListView
                    .getSelectionModel()
                    .select(nuovaPlaylist);

            AlertUtil.showInfo(
                    "Playlist creata",
                    "Aggiunte "
                            + nuovaPlaylist.getTracks().size()
                            + " tracce alla playlist."
            );

            refreshAssignablePlaylists();

            autoPlaylistFilterInput.clear();
            commandManager.clearHistory();
            saveLibraryAsync();

        } catch (PlaylistValidationException e) {
            AlertUtil.showError(
                    e.getHeader(),
                    e.getMessage()
            );
        }
    }

    private void refreshAssignablePlaylists() {
        FilteredList<Playlist> normalPlaylistsOnly =
                new FilteredList<>(
                        libreria.getPlaylists(),
                        playlist ->
                                !playlistService.isSmartPlaylist(playlist)
                                        && !playlist.isGenerataAutomaticamente()
                );

        playlistComboBox.setItems(normalPlaylistsOnly);
    }
}