package org.example.mediamusicplayer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.util.AlertUtil;
import org.example.mediamusicplayer.exception.TrackValidationException;

import java.time.Year;

public class MusicPlayerController {

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

    private ObservableList<Track> listaCanzoni;

    // Istanziamo il nostro Service per delegargli la logica
    private TrackService trackService = new TrackService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("formattedLength"));

        listaCanzoni = FXCollections.observableArrayList();
        trackTable.setItems(listaCanzoni);
    }

    @FXML
    public void onAddTrackClick() {
        try {
            // IL CONTROLLER FA SOLO DA PASSACARTE:
            // Prende il testo dall'interfaccia e lo manda al Service.
            Track nuovaTraccia = trackService.createTrack(
                    titleInput.getText(),
                    authorInput.getText(),
                    lengthInput.getText(),
                    genreInput.getText(),
                    yearInput.getText()
            );

            // Se il Service non ha lanciato eccezioni, aggiungiamo la traccia!
            listaCanzoni.add(nuovaTraccia);

            // Pulizia
            titleInput.clear();
            authorInput.clear();
            lengthInput.clear();
            genreInput.clear();
            yearInput.clear();

        } catch (TrackValidationException e) {
            // SE IL SERVICE TROVA UN ERRORE, USIAMO LA CLASSE UTIL PER MOSTRARLO!
            AlertUtil.showError(e.getHeader(), e.getMessage());
        }
    }

    @FXML
    public void onDeleteTrackClick() {
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();

        if (tracciaSelezionata != null) {
            listaCanzoni.remove(tracciaSelezionata);
        } else {
            // Usiamo il nostro AlertUtil esterno!
            AlertUtil.showError("Nessuna selezione", "Per favore, clicca su una traccia nella tabella prima di cliccare su Elimina.");
        }
    }
}