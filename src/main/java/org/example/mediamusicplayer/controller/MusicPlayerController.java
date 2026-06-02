package org.example.mediamusicplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.MusicLibraryService; // <-- IMPORTATO
import org.example.mediamusicplayer.util.AlertUtil;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.exception.PlaylistValidationException;

import java.time.Year;

public class MusicPlayerController {

    @FXML private ListView<Playlist> playlistListView;
    @FXML private TextField newPlaylistInput;
    @FXML private Label currentPlaylistLabel;

    // Menu a tendina per assegnare le tracce
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
    private Playlist playlistAttuale;      // Se null, significa che stiamo guardando "Tutte le tracce"

    // I nostri Service per la logica pura
    private TrackService trackService;
    private PlaylistService playlistService;
    private MusicLibraryService libraryService; // <-- AGGIUNTO

    @FXML
    public void initialize() {
        // Inizializziamo i Service e la libreria
        trackService = new TrackService();
        playlistService = new PlaylistService();
        libraryService = new MusicLibraryService(); // <-- INIZIALIZZATO
        libreria = new MusicLibrary();

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLength"));

        playlistListView.setItems(libreria.getPlaylists());

        // Colleghiamo le stesse playlist anche al menu a tendina in basso!
        playlistComboBox.setItems(libreria.getPlaylists());

        // All'avvio, mostriamo il magazzino globale
        trackTable.setItems(libreria.getAllTracks());

        playlistListView.getSelectionModel().selectedItemProperty().addListener((obs, vecchia, nuova) -> {
            if (nuova != null) {
                playlistAttuale = nuova;
                currentPlaylistLabel.setText("Stai ascoltando Playlist: " + playlistAttuale.getName());
                trackTable.setItems(playlistAttuale.getTracks());
            }
        });
    }

    // --- TORNA A "TUTTE LE TRACCE" ---
    @FXML
    public void onViewAllTracksClick() {
        playlistAttuale = null; // Resettiamo la selezione
        playlistListView.getSelectionModel().clearSelection(); // Deselezioniamo a sinistra
        trackTable.setItems(libreria.getAllTracks()); // Mostriamo tutto il magazzino
        currentPlaylistLabel.setText("Gestione Tracce: Tutte le canzoni");
    }

    // --- CREA PLAYLIST ---
    @FXML
    public void onAddPlaylistClick() {
        try {
            // Deleghiamo al Service la creazione e il controllo dei duplicati
            Playlist nuovaPlaylist = playlistService.createPlaylist(newPlaylistInput.getText(), libreria);

            // ORA USIAMO IL NUOVO SERVICE DELLA LIBRERIA
            libraryService.addPlaylist(libreria, nuovaPlaylist);

            newPlaylistInput.clear();
            playlistListView.getSelectionModel().select(nuovaPlaylist);

        } catch (PlaylistValidationException e) {
            // Se c'è un errore (nome vuoto o duplicato), mostriamo il pop-up
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    // --- CREA TRACCIA ---
    @FXML
    public void onAddTrackClick() {
        try {
            Track nuovaTraccia = trackService.createTrack(
                    titleInput.getText(), authorInput.getText(),
                    lengthInput.getText(), genreInput.getText(), yearInput.getText()
            );

            // ORA USIAMO IL NUOVO SERVICE PER AGGIUNGERE LA TRACCIA AL MAGAZZINO
            libraryService.addTrackToLibrary(libreria, nuovaTraccia);

            // Se l'utente in questo momento sta visualizzando una specifica playlist,
            // la aggiungiamo automaticamente anche a quella!
            if (playlistAttuale != null) {
                playlistAttuale.addTrack(nuovaTraccia);
            }

            titleInput.clear(); authorInput.clear(); lengthInput.clear();
            genreInput.clear(); yearInput.clear();

        } catch (TrackValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    // --- ASSEGNA A PLAYLIST DA MENU A TENDINA ---
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

        // Se la traccia non è già nella playlist, la aggiungiamo
        if (!playlistScelta.getTracks().contains(tracciaSelezionata)) {
            playlistScelta.addTrack(tracciaSelezionata);

            // Un piccolo feedback visivo per confermare
            javafx.scene.control.Alert info = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            info.setTitle("Fatto!");
            info.setHeaderText(null);
            info.setContentText("Traccia aggiunta a " + playlistScelta.getName());
            info.showAndWait();
        } else {
            AlertUtil.showError("Già presente", "Questa traccia è già presente in questa playlist.");
        }
    }

    // --- ELIMINA TRACCIA ---
    @FXML
    public void onDeleteTrackClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();

        if (tracciaSelezionata == null) {
            AlertUtil.showError("Nessuna selezione", "Clicca su una traccia prima di eliminarla.");
            return;
        }

        if (playlistAttuale == null) {
            // SOGLIA DI ATTENZIONE ALTA: Siamo nella "Gestione Tutte le tracce".
            // ORA DELEGHIAMO L'ELIMINAZIONE GLOBALE (A CASCATA) AL SERVICE!
            libraryService.deleteTrackGlobal(libreria, tracciaSelezionata);

        } else {
            // Rimuoviamo la canzone SOLO dalla playlist che stiamo guardando
            playlistAttuale.removeTrack(tracciaSelezionata);
        }
    }
}