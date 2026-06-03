package org.example.mediamusicplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.util.AlertUtil;
import java.time.Year;

public class MusicPlayerController {

    @FXML private ListView<Playlist> playlistListView;
    @FXML private TextField newPlaylistInput;
    @FXML private Label currentPlaylistLabel;

    @FXML private ComboBox<Playlist> playlistComboBox;

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

    @FXML
    public void initialize() {
        inizializzaService();
        inizializzaLibreria();
        inizializzaTabellaTracce();
        inizializzaComponentiPlaylist();
        inizializzaListenerPlaylist();
    }

    private void inizializzaService() {
        trackService = new TrackService();
        playlistService = new PlaylistService();
        libraryService = new MusicLibraryService();
    }

    private void inizializzaLibreria() {
        libreria = new MusicLibrary();
        playlistAttuale = null;
    }

    private void inizializzaTabellaTracce() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLength"));

        trackTable.setItems(libreria.getAllTracks());
    }

    private void inizializzaComponentiPlaylist() {
        playlistListView.setItems(libreria.getPlaylists());
        playlistComboBox.setItems(libreria.getPlaylists());
        currentPlaylistLabel.setText("Tracce disponibili");
    }

    private void inizializzaListenerPlaylist() {
        playlistListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, vecchiaPlaylist, nuovaPlaylist) -> {
                    if (nuovaPlaylist != null) {
                        mostraPlaylist(nuovaPlaylist);
                    }
                });
    }

    @FXML
    public void onViewAllTracksClick() {
        playlistAttuale = null;
        playlistListView.getSelectionModel().clearSelection();
        trackTable.setItems(libreria.getAllTracks());
        currentPlaylistLabel.setText("Tracce disponibili");
    }

    @FXML
    public void onAddPlaylistClick() {
        try {
            Playlist nuovaPlaylist = playlistService.createPlaylist(
                    newPlaylistInput.getText(),
                    libreria
            );

            libraryService.addPlaylist(libreria, nuovaPlaylist);

            newPlaylistInput.clear();
            playlistListView.getSelectionModel().select(nuovaPlaylist);

        } catch (PlaylistValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onAddTrackClick() {
        try {
            Track nuovaTraccia = trackService.createTrack(
                    titleInput.getText(),
                    authorInput.getText(),
                    lengthInput.getText(),
                    genreInput.getText(),
                    yearInput.getText()
            );

            libraryService.addTrackToLibrary(libreria, nuovaTraccia);

            if (playlistAttuale != null) {
                playlistService.addTrackToPlaylist(playlistAttuale, nuovaTraccia);
            }

            pulisciCampiTraccia();

        } catch (TrackValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onAssignToPlaylistClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        Playlist playlistScelta = playlistComboBox.getValue();

        if (tracciaSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna traccia selezionata",
                    "Seleziona prima una traccia dalla tabella."
            );
            return;
        }

        if (playlistScelta == null) {
            AlertUtil.showError(
                    "Nessuna playlist selezionata",
                    "Seleziona una playlist dal menu a tendina."
            );
            return;
        }

        if (playlistService.containsTrack(playlistScelta, tracciaSelezionata)) {
            AlertUtil.showError(
                    "Già presente",
                    "Questa traccia è già presente in questa playlist."
            );
            return;
        }

        playlistService.addTrackToPlaylist(playlistScelta, tracciaSelezionata);

        AlertUtil.showInfo(
                "Fatto!",
                "Traccia aggiunta a " + playlistScelta.getName()
        );
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
            AlertUtil.showError(
                    "Nessuna selezione",
                    "Clicca su una traccia prima di eliminarla."
            );
            return;
        }

        if (playlistAttuale == null) {
            libraryService.deleteTrackGlobal(libreria, tracciaSelezionata);

            AlertUtil.showInfo(
                    "Traccia eliminata",
                    "La traccia è stata rimossa dalla libreria e da tutte le playlist."
            );

            mostraTutteLeTracce();
        } else {
            boolean rimossa = playlistService.removeTrackFromPlaylist(
                    playlistAttuale,
                    tracciaSelezionata
            );

            if (rimossa) {
                AlertUtil.showInfo(
                        "Traccia rimossa",
                        "La traccia è stata rimossa dalla playlist " + playlistAttuale.getName() + "."
                );

                aggiornaVistaPlaylistAttuale();
            } else {
                AlertUtil.showError(
                        "Traccia non trovata",
                        "La traccia selezionata non è presente nella playlist corrente."
                );
            }
        }
    }

    @FXML
    public void onRemoveTrackFromPlaylistClick() {
        if (playlistAttuale == null) {
            AlertUtil.showError(
                    "Nessuna playlist selezionata",
                    "Per rimuovere una traccia devi prima selezionare una playlist dalla barra laterale."
            );
            return;
        }

        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();

        if (tracciaSelezionata == null) {
            AlertUtil.showError(
                    "Nessuna traccia selezionata",
                    "Per favore, seleziona una traccia dalla playlist prima di cliccare su Rimuovi da Playlist."
            );
            return;
        }

        boolean rimossa = playlistService.removeTrackFromPlaylist(
                playlistAttuale,
                tracciaSelezionata
        );

        if (rimossa) {
            AlertUtil.showInfo(
                    "Traccia rimossa",
                    "La traccia è stata rimossa dalla playlist " + playlistAttuale.getName() + "."
            );

            aggiornaVistaPlaylistAttuale();
        } else {
            AlertUtil.showError(
                    "Traccia non trovata",
                    "La traccia selezionata non è presente nella playlist corrente."
            );
        }
    }

    private void aggiornaVistaPlaylistAttuale() {
        if (playlistAttuale != null) {
            trackTable.setItems(FXCollections.observableArrayList(playlistAttuale.getTracks()));
        }
    }

    private void mostraTutteLeTracce() {
        trackTable.setItems(libreria.getAllTracks());
        currentPlaylistLabel.setText("Tracce disponibili");
    }

    private void mostraPlaylist(Playlist playlist) {
        playlistAttuale = playlist;
        currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
        trackTable.setItems(FXCollections.observableArrayList(playlistAttuale.getTracks()));
    }

    private void pulisciCampiTraccia() {
        titleInput.clear();
        authorInput.clear();
        lengthInput.clear();
        genreInput.clear();
        yearInput.clear();
    }
}