package org.example.mediamusicplayer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.mediamusicplayer.exception.PlaylistValidationException;
import org.example.mediamusicplayer.exception.TrackValidationException;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.util.AlertUtil;

import java.time.Year;

public class MusicPlayerController {

    // --- TABELLA TRACCE ---

    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, String> lengthColumn;
    @FXML private TableColumn<Track, String> genreColumn;
    @FXML private TableColumn<Track, Year> yearColumn;

    // --- INPUT TRACCE ---

    @FXML private TextField titleInput;
    @FXML private TextField authorInput;
    @FXML private TextField lengthInput;
    @FXML private TextField genreInput;
    @FXML private TextField yearInput;

    // --- TABELLA PLAYLIST ---

    @FXML private TableView<Playlist> playlistTable;
    @FXML private TableColumn<Playlist, String> playlistNameColumn;
    @FXML private TableColumn<Playlist, Integer> playlistTracksColumn;

    // --- INPUT PLAYLIST ---

    @FXML private TextField playlistNameInput;

    // --- LISTE OSSERVABILI ---

    private ObservableList<Track> listaCanzoni;
    private ObservableList<Playlist> listaPlaylist;

    // --- SERVICE ---

    private final TrackService trackService = new TrackService();
    private final PlaylistService playlistService = new PlaylistService();

    @FXML
    public void initialize() {

        // Configurazione colonne tracce
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLength"));

        listaCanzoni = FXCollections.observableArrayList();
        trackTable.setItems(listaCanzoni);

        // Configurazione colonne playlist
        playlistNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        playlistTracksColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfTracks"));

        listaPlaylist = FXCollections.observableArrayList();
        playlistTable.setItems(listaPlaylist);
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

            listaCanzoni.add(nuovaTraccia);

            titleInput.clear();
            authorInput.clear();
            lengthInput.clear();
            genreInput.clear();
            yearInput.clear();

        } catch (TrackValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onDeleteTrackClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();

        if (tracciaSelezionata != null) {
            listaCanzoni.remove(tracciaSelezionata);
        } else {
            AlertUtil.showError(
                    "Nessuna selezione",
                    "Per favore, clicca su una traccia nella tabella prima di cliccare su Elimina."
            );
        }
    }

    @FXML
    public void onAddPlaylistClick() {
        try {
            Playlist nuovaPlaylist = playlistService.createPlaylist(
                    playlistNameInput.getText(),
                    listaPlaylist
            );

            listaPlaylist.add(nuovaPlaylist);
            playlistNameInput.clear();

        } catch (PlaylistValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onDeletePlaylistClick() {
        try {
            Playlist playlistSelezionata = playlistTable.getSelectionModel().getSelectedItem();

            playlistService.deletePlaylist(playlistSelezionata);

            listaPlaylist.remove(playlistSelezionata);

        } catch (PlaylistValidationException e) {
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }
}