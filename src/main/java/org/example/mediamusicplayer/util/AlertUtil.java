package org.example.mediamusicplayer.util;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

public class AlertUtil {

    // Metodo statico: lo chiami da ovunque senza dover fare "new AlertUtil()"
    public static void showError(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di Inserimento");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);

        // Risolve il problema del testo tagliato
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}