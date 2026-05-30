module org.example.mediamusicplayer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.mediamusicplayer to javafx.fxml;
    exports org.example.mediamusicplayer;
}