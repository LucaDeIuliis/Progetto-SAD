package org.example.mediamusicplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.util.AlertUtil;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.exception.PlaylistValidationException;

import java.time.Year;
import java.util.Optional;

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
        trackService = new TrackService();
        playlistService = new PlaylistService();
        libraryService = new MusicLibraryService();
        libreria = new MusicLibrary();

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLength"));

        playlistListView.setItems(libreria.getPlaylists());
        playlistComboBox.setItems(libreria.getPlaylists());
        trackTable.setItems(libreria.getAllTracks());

        // Ascoltatore per il cambio playlist
        playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                playlistAttuale = nuova;
                currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
                trackTable.setItems(playlistAttuale.getTracks());
            }
        });

        // Ascoltatore per auto-compilare i campi quando si seleziona una traccia
        trackTable.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                titleInput.setText(nuova.getTitle());
                authorInput.setText(nuova.getAuthor());
                genreInput.setText(nuova.getGenre());
                yearInput.setText(String.valueOf(nuova.getYear().getValue()));

                long totalSeconds = nuova.getLength().getSeconds();
                lengthInput.setText(String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60));
            }
        });
    }

    @FXML
    public void onViewAllTracksClick() {
        playlistAttuale = null;
        playlistListView.getSelectionModel().clearSelection();
        trackTable.setItems(libreria.getAllTracks());
        currentPlaylistLabel.setText("Gestione Tracce: Tutte le canzoni");
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
            AlertUtil.showError("Nessuna selezione", "Seleziona la playlist che vuoi rinominare dalla barra laterale.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(playlistSelezionata.getName());
        dialog.setTitle("Rinomina Playlist");
        dialog.setHeaderText("Stai modificando: " + playlistSelezionata.getName());
        dialog.setContentText("Inserisci il nuovo nome:");

        Optional<String> result = dialog.showAndWait();
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
            // Creiamo la traccia
            Track nuovaTraccia = trackService.createTrack(
                    titleInput.getText(), authorInput.getText(),
                    lengthInput.getText(), genreInput.getText(), yearInput.getText(),
                    libreria
            );

            // Aggiungiamo al magazzino e all'eventuale playlist
            libraryService.addTrackToLibrary(libreria, nuovaTraccia);
            if (playlistAttuale != null) {
                playlistAttuale.addTrack(nuovaTraccia);
            }

            // Svuotiamo i campi di input
            titleInput.clear(); authorInput.clear(); lengthInput.clear();
            genreInput.clear(); yearInput.clear();

            // ---> ECCO LA RIGA MAGICA CHE RISOLVE IL BUG <---
            trackTable.getSelectionModel().clearSelection();

        } catch (TrackValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    // MODIFICA TRACCIA
    @FXML
    public void onUpdateTrackClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();

        if (tracciaSelezionata == null) {
            AlertUtil.showError("Nessuna selezione", "Seleziona una traccia dalla tabella per modificarla.");
            return;
        }

        try {
            // AGGIUNTO 'libreria' COME ULTIMO PARAMETRO
            trackService.updateTrack(
                    tracciaSelezionata, titleInput.getText(), authorInput.getText(),
                    lengthInput.getText(), genreInput.getText(), yearInput.getText(),
                    libreria
            );

            trackTable.refresh();

            titleInput.clear(); authorInput.clear(); lengthInput.clear();
            genreInput.clear(); yearInput.clear();
            trackTable.getSelectionModel().clearSelection();

        } catch (TrackValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onAssignToPlaylistClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();
        Playlist playlistScelta = playlistComboBox.getValue();

        if (tracciaSelezionata == null) {
            AlertUtil.showError("Nessuna Traccia", "Seleziona prima una traccia dalla tabella.");
            return;
        }
        if (playlistScelta == null) {
            AlertUtil.showError("Nessuna Playlist", "Seleziona una playlist dal menu a tendina.");
            return;
        }

        if (!playlistScelta.getTracks().contains(tracciaSelezionata)) {
            playlistScelta.addTrack(tracciaSelezionata);
            javafx.scene.control.Alert info = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            info.setTitle("Fatto!");
            info.setHeaderText(null);
            info.setContentText("Traccia aggiunta a " + playlistScelta.getName());
            info.showAndWait();
        } else {
            AlertUtil.showError("Già presente", "Questa traccia è già presente in questa playlist.");
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

        // 1. Eliminiamo la traccia
        if (playlistAttuale == null) {
            libraryService.deleteTrackGlobal(libreria, tracciaSelezionata);
        } else {
            playlistAttuale.removeTrack(tracciaSelezionata);
        }

        // 2. NUOVO: Svuotiamo i campi di compilazione dopo l'eliminazione!
        titleInput.clear();
        authorInput.clear();
        lengthInput.clear();
        genreInput.clear();
        yearInput.clear();

        // 3. Rimuoviamo la selezione "fantasma" dalla tabella
         trackTable.getSelectionModel().clearSelection();
    }
}