package org.example.mediamusicplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.mediamusicplayer.controller.MusicPlayerController;

import java.io.IOException;

public class MainPlayerApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainPlayerApplication.class.getResource("player-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        MusicPlayerController controller = fxmlLoader.getController();

        stage.setTitle("Music Playlist Manager");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> controller.shutdown());

        stage.show();
    }
}