package org.example.mediamusicplayer.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.example.mediamusicplayer.service.statistics.PlaybackStatisticsService;

import java.util.List;

public class StatisticsController {

    @FXML private ListView<HBox> mostPlayedTracksListView;
    @FXML private ListView<HBox> mostPlayedPlaylistsListView;

    @FXML private Label emptyTracksLabel;
    @FXML private Label emptyPlaylistsLabel;

    private final MusicLibrary libreria;
    private final PlaybackStatisticsService playbackStatisticsService;
    private final Scene playerScene;

    @FXML
    public void initialize() {
        configureListViewStyle();
        updateStatistics();
    }
    public StatisticsController(MusicLibrary libreria, PlaybackStatisticsService playbackStatisticsService, Scene playerScene) {
        this.libreria = libreria;
        this.playbackStatisticsService = playbackStatisticsService;
        this.playerScene = playerScene;
    }

    private void updateStatistics() {
        if (libreria == null || playbackStatisticsService == null) {
            return;
        }

        updateMostPlayedTracks();
        updateMostPlayedPlaylists();
    }

    private void updateMostPlayedTracks() {
        List<Track> tracks =
                playbackStatisticsService.getMostPlayedTracks(libreria, 10);

        mostPlayedTracksListView.getItems().clear();

        if (tracks.isEmpty()) {
            emptyTracksLabel.setVisible(true);
            emptyTracksLabel.setManaged(true);

            mostPlayedTracksListView.setVisible(false);
            mostPlayedTracksListView.setManaged(false);
            return;
        }

        emptyTracksLabel.setVisible(false);
        emptyTracksLabel.setManaged(false);

        mostPlayedTracksListView.setVisible(true);
        mostPlayedTracksListView.setManaged(true);

        mostPlayedTracksListView.getItems().add(
                createHeaderRow("Ascolti", "Brano", "Autore")
        );

        for (Track track : tracks) {
            mostPlayedTracksListView.getItems().add(
                    createTrackStatisticRow(track)
            );
        }
    }

    private void updateMostPlayedPlaylists() {
        List<Playlist> playlists =
                playbackStatisticsService.getMostPlayedPlaylists(libreria, 10);

        mostPlayedPlaylistsListView.getItems().clear();

        if (playlists.isEmpty()) {
            emptyPlaylistsLabel.setVisible(true);
            emptyPlaylistsLabel.setManaged(true);

            mostPlayedPlaylistsListView.setVisible(false);
            mostPlayedPlaylistsListView.setManaged(false);
            return;
        }

        emptyPlaylistsLabel.setVisible(false);
        emptyPlaylistsLabel.setManaged(false);

        mostPlayedPlaylistsListView.setVisible(true);
        mostPlayedPlaylistsListView.setManaged(true);

        mostPlayedPlaylistsListView.getItems().add(
                createHeaderRow("Riproduzioni", "Playlist", "")
        );

        for (Playlist playlist : playlists) {
            mostPlayedPlaylistsListView.getItems().add(
                    createPlaylistStatisticRow(playlist)
            );
        }
    }

    private HBox createHeaderRow(String firstColumn, String secondColumn, String thirdColumn) {
        HBox header = new HBox(0);
        header.setPadding(new Insets(8, 12, 8, 12));
        header.setStyle("-fx-background-color: #eceff1; -fx-background-radius: 8;");

        Label firstLabel = new Label(firstColumn);
        firstLabel.setPrefWidth(120);
        firstLabel.setMinWidth(120);
        firstLabel.setMaxWidth(120);
        firstLabel.setAlignment(Pos.CENTER);
        firstLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-border-color: transparent #cfd8dc transparent transparent;" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Label secondLabel = new Label(secondColumn);
        secondLabel.setPrefWidth(120);
        secondLabel.setMinWidth(120);
        secondLabel.setMaxWidth(120);
        secondLabel.setAlignment(Pos.CENTER);
        secondLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-border-color: transparent #cfd8dc transparent transparent;" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Label thirdLabel = new Label(thirdColumn);
        thirdLabel.setPrefWidth(120);
        thirdLabel.setMinWidth(120);
        thirdLabel.setMaxWidth(120);
        thirdLabel.setAlignment(Pos.CENTER);
        thirdLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;"
        );

        header.getChildren().addAll(firstLabel, secondLabel, thirdLabel);

        return header;
    }

    private HBox createTrackStatisticRow(Track track) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

        Label playCountLabel = new Label(track.getPlayCount() + " ascolti");
        playCountLabel.setPrefWidth(120);
        playCountLabel.setMinWidth(120);
        playCountLabel.setMaxWidth(120);
        playCountLabel.setAlignment(Pos.CENTER);
        playCountLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #455A64;" +
                        "-fx-border-color: transparent #dddddd transparent transparent;" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Label titleLabel = new Label(track.getTitle());
        titleLabel.setPrefWidth(120);
        titleLabel.setMinWidth(120);
        titleLabel.setMaxWidth(120);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-border-color: transparent #dddddd transparent transparent;" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Label authorLabel = new Label(track.getAuthor());
        authorLabel.setPrefWidth(120);
        authorLabel.setMinWidth(120);
        authorLabel.setMaxWidth(120);
        authorLabel.setAlignment(Pos.CENTER);
        authorLabel.setStyle(
                "-fx-text-fill: #757575;"
        );

        row.getChildren().addAll(
                playCountLabel,
                titleLabel,
                authorLabel
        );

        return row;
    }

    private HBox createPlaylistStatisticRow(Playlist playlist) {
        HBox row = new HBox(0);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 8;");

        Label playCountLabel = new Label(playlist.getPlayCount() + " ripr.");
        playCountLabel.setPrefWidth(120);
        playCountLabel.setMinWidth(120);
        playCountLabel.setMaxWidth(120);
        playCountLabel.setAlignment(Pos.CENTER);
        playCountLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #455A64;" +
                        "-fx-border-color: transparent #dddddd transparent transparent;" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Label playlistNameLabel = new Label(playlist.getName());
        playlistNameLabel.setPrefWidth(120);
        playlistNameLabel.setMinWidth(120);
        playlistNameLabel.setMaxWidth(120);
        playlistNameLabel.setAlignment(Pos.CENTER);
        playlistNameLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #263238;" +
                        "-fx-border-color: transparent #dddddd transparent transparent;" +
                        "-fx-border-width: 0 1 0 0;"
        );

        Label emptyLabel = new Label("");
        emptyLabel.setPrefWidth(120);
        emptyLabel.setMinWidth(120);
        emptyLabel.setMaxWidth(120);
        row.getChildren().addAll(
                playCountLabel,
                playlistNameLabel,
                emptyLabel
        );

        return row;
    }

    private void configureListViewStyle() {
        mostPlayedTracksListView.setCellFactory(listView -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(null);
                    setGraphic(item);
                    setStyle("-fx-background-color: transparent; -fx-padding: 6;");
                }
            }
        });

        mostPlayedPlaylistsListView.setCellFactory(listView -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(null);
                    setGraphic(item);
                    setStyle("-fx-background-color: transparent; -fx-padding: 6;");
                }
            }
        });
    }

    @FXML
    public void onBackClick() {
        if (playerScene == null) {
            return;
        }

        Stage stage =
                (Stage) mostPlayedTracksListView.getScene().getWindow();

        stage.setScene(playerScene);
        stage.setTitle("Media Music Player");
    }
}