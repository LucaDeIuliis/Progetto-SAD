package org.example.mediamusicplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.mediamusicplayer.controller.MusicPlayerController;
import org.example.mediamusicplayer.persistence.DatabaseInitializer;
import org.example.mediamusicplayer.persistence.DatabaseManager;
import org.example.mediamusicplayer.repository.MusicLibraryRepository;
import org.example.mediamusicplayer.repository.PlaylistRepository;
import org.example.mediamusicplayer.repository.TrackRepository;
import org.example.mediamusicplayer.service.persistence.PersistenceService;
import org.example.mediamusicplayer.service.AudioPlayerService;
import org.example.mediamusicplayer.service.MusicLibraryService;
import org.example.mediamusicplayer.service.PlaylistService;
import org.example.mediamusicplayer.service.TrackService;
import org.example.mediamusicplayer.service.statistics.PlaybackStatisticsService;
import java.io.IOException;
import org.example.mediamusicplayer.service.playlistnavigation.PlaylistNavigationService;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.service.command.CommandManager;

public class MainPlayerApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();

        DatabaseInitializer databaseInitializer =
                new DatabaseInitializer(databaseManager);
        databaseInitializer.initializeDatabase();

        TrackRepository trackRepository =
                new TrackRepository(databaseManager);

        PlaylistRepository playlistRepository =
                new PlaylistRepository(databaseManager);

        MusicLibraryRepository musicLibraryRepository =
                new MusicLibraryRepository(
                        databaseManager,
                        trackRepository,
                        playlistRepository
                );

        PersistenceService persistenceService =
                new PersistenceService(
                        musicLibraryRepository,
                        trackRepository,
                        playlistRepository
                );

        TrackService trackService = new TrackService();
        PlaylistService playlistService = new PlaylistService();
        MusicLibraryService libraryService = new MusicLibraryService();
        AudioPlayerService audioPlayerService = new AudioPlayerService();
        MusicLibrary musicLibrary = new MusicLibrary();
        CommandManager commandManager = new CommandManager();
        PlaybackStatisticsService playbackStatisticsService = new PlaybackStatisticsService();
        PlaylistNavigationService playlistNavigationService =  new PlaylistNavigationService();

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainPlayerApplication.class.getResource("player-view.fxml")
        );

        fxmlLoader.setControllerFactory(controllerClass -> {
            if (controllerClass == MusicPlayerController.class) {
                return new MusicPlayerController(
                        musicLibrary,
                        commandManager,
                        trackService,
                        playlistService,
                        libraryService,
                        audioPlayerService,
                        playbackStatisticsService,
                        playlistNavigationService,
                        persistenceService
                );
            }
            try {
                return controllerClass
                        .getDeclaredConstructor()
                        .newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(
                        "Impossibile creare il controller "
                                + controllerClass.getName(),
                        e
                );
            }
        });

        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        MusicPlayerController controller = fxmlLoader.getController();

        stage.setTitle("Music Playlist Manager");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> controller.shutdown());
        stage.show();
    }
}