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
        String title = titleInput.getText();
        String author = authorInput.getText();
        String lengthStr = lengthInput.getText();
        String genre = genreInput.getText();
        String yearStr = yearInput.getText();

        // ==========================================
        // FASE 1: CONTROLLO CAMPI VUOTI (Uno per uno)
        // ==========================================
        if (title == null || title.trim().isEmpty()) {
            mostraErrore("Titolo mancante", "Per favore, inserisci il titolo della traccia.");
            return;
        }
        if (author == null || author.trim().isEmpty()) {
            mostraErrore("Autore mancante", "Per favore, inserisci l'autore della traccia.");
            return;
        }
        if (genre == null || genre.trim().isEmpty()) {
            mostraErrore("Genere mancante", "Per favore, inserisci il genere musicale.");
            return;
        }
        if (yearStr == null || yearStr.trim().isEmpty()) {
            mostraErrore("Anno mancante", "Per favore, inserisci l'anno di pubblicazione.");
            return;
        }
        if (lengthStr == null || lengthStr.trim().isEmpty()) {
            mostraErrore("Durata mancante", "Per favore, inserisci la durata della traccia.");
            return;
        }

        // ==========================================
        // FASE 2: GESTIONE ANNO
        // ==========================================
        Year year;
        try {
            year = Year.parse(yearStr);
        } catch (Exception e) {
            mostraErrore("Formato Anno Errato", "L'anno deve essere un numero valido (es. 2023). Hai inserito delle lettere?");
            return; // Blocca se l'utente ha scritto lettere
        }

        Year annoCorrente = Year.now();
        if (year.getValue() < 0) {
            mostraErrore("Anno negativo", "L'anno di pubblicazione non può essere un numero negativo.");
            return;
        }
        if (year.isAfter(annoCorrente)) {
            mostraErrore("Anno dal futuro", "L'anno non può essere nel futuro! Inserisci un anno fino al " + annoCorrente.getValue() + ".");
            return;
        }

        // ==========================================
        // FASE 3: GESTIONE DURATA
        // ==========================================
        long totalSeconds = 0;
        try {
            if (lengthStr.contains(":")) {
                String[] parts = lengthStr.split(":");
                // Controllo extra: assicuriamoci che abbia scritto mm:ss (es. non 3:45:10)
                if (parts.length != 2) throw new NumberFormatException();

                long min = Long.parseLong(parts[0]);
                long sec = Long.parseLong(parts[1]);

                // Controllo extra sui secondi (non possono essere es. 3:75)
                if (sec >= 60 || sec < 0 || min < 0) throw new NumberFormatException();

                totalSeconds = (min * 60) + sec;
            } else {
                totalSeconds = Long.parseLong(lengthStr);
            }
        } catch (Exception e) {
            mostraErrore("Formato Durata Errato",
                    "La durata deve essere in formato minuti:secondi(es. 3:45)\n" +
                            "oppure in secondi totali (es. 225).\n\n" +
                            "Assicurati di non aver inserito lettere o simboli\n"+
                    "e che i minuti e i secondi siano compresi tra 0-59");
            return; // Blocca se il formato non è un numero o è un formato non riconosciuto
        }

        if (totalSeconds < 1) {
            mostraErrore("Durata non valida", "La durata della traccia non può essere zero o negativa!");
            return;
        }
        Duration length = Duration.ofSeconds(totalSeconds);

        // ==========================================
        // FASE 4: INSERIMENTO E PULIZIA
        // ==========================================
        Track nuovaTraccia = new Track(title, author, length, genre, year);
        listaCanzoni.add(nuovaTraccia);

        titleInput.clear();
        authorInput.clear();
        lengthInput.clear();
        genreInput.clear();
        yearInput.clear();
    }

    // --- METODO DI SUPPORTO PER CREARE I POP-UP ---
    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di Inserimento");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
    // --- METODO DEL BOTTONE "ELIMINA" ---
    @FXML
    public void onDeleteTrackClick() {
        // 1. Chiediamo alla tabella quale elemento è attualmente cliccato/selezionato
        Track tracciaSelezionata = trackTable.getSelectionModel().getSelectedItem();

        // 2. Controlliamo se l'utente ha effettivamente selezionato qualcosa
        if (tracciaSelezionata != null) {
            // Se c'è una traccia selezionata, la rimuoviamo dalla lista
            listaCanzoni.remove(tracciaSelezionata);
        } else {
            // Se ha cliccato "Elimina" senza selezionare nulla, mostriamo un avviso
            mostraErrore("Nessuna selezione", "Per favore, clicca su una traccia nella tabella prima di cliccare su Elimina.");
        }
    }
}