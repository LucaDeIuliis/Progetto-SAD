package org.example.mediamusicplayer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MusicPlayerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("PORCAMADONNA INIZIAMO A FARE QUALCOSA");
    }
}
