package org.example.mediamusicplayer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.mediamusicplayer.model.Track;

import java.time.Duration;
import java.time.Year;

public class MusicPlayerController {

    // --- COLLEGAMENTI ALLA TABELLA ---
    @FXML private TableView<Track> trackTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> authorColumn;
    @FXML private TableColumn<Track, String> lengthColumn;
    @FXML private TableColumn<Track, String> genreColumn;
    @FXML private TableColumn<Track, Year> yearColumn;

    // --- COLLEGAMENTI ALLE CASELLE DI TESTO ---
    @FXML private TextField titleInput;
    @FXML private TextField authorInput;
    @FXML private TextField lengthInput;
    @FXML private TextField genreInput;
    @FXML private TextField yearInput;

    private ObservableList<Track> listaCanzoni;

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
            String title = titleInput.getText();
            String author = authorInput.getText();
            String lengthStr = lengthInput.getText();
            String genre = genreInput.getText();
            String yearStr = yearInput.getText();

            // 1. Controllo campi vuoti
            if (title == null || title.trim().isEmpty() ||
                    author == null || author.trim().isEmpty() ||
                    genre == null || genre.trim().isEmpty()) {

                mostraErrore("Dati Mancanti", "Il Titolo, l'Autore e il Genere sono campi obbligatori!");
                return;
            }

            // 2. CONVERSIONE E CONTROLLO ANNO
            Year year = Year.parse(yearStr);
            if (year.getValue() < 0) {
                mostraErrore("Anno non valido", "L'anno non può essere un valore negativo!");
                return; // Blocchiamo l'inserimento
            }

            // 3. CONVERSIONE E CONTROLLO DURATA
            long totalSeconds = 0;
            if (lengthStr.contains(":")) {
                String[] parts = lengthStr.split(":");
                long min = Long.parseLong(parts[0]);
                long sec = Long.parseLong(parts[1]);
                totalSeconds = (min * 60) + sec;
            } else {
                totalSeconds = Long.parseLong(lengthStr);
            }

            if (totalSeconds < 1) {
                mostraErrore("Durata non valida", "La durata della traccia non può essere negativa o zero!");
                return; // Blocchiamo l'inserimento
            }
            Duration length = Duration.ofSeconds(totalSeconds);

            // 4. Creazione e aggiunta
            Track nuovaTraccia = new Track(title, author, length, genre, year);
            listaCanzoni.add(nuovaTraccia);

            // 5. Pulizia
            titleInput.clear();
            authorInput.clear();
            lengthInput.clear();
            genreInput.clear();
            yearInput.clear();

        } catch (Exception e) {
            mostraErrore("Tipo di dati sbagliati",
                    "Attenzione! Hai inserito del testo non valido in un campo numerico oppure quest'ultimo non risulta compilato.\n\n" +
                            "- Anno: deve essere un numero positivo (es. 2023)\n" +
                            "- Durata: deve essere in formato minuti:secondi (es. 3:45) o in secondi totali (es. 225)");
        }
    }

    // --- METODO DI SUPPORTO PER CREARE I POP-UP ---
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}