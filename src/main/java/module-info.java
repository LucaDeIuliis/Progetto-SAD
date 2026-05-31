module org.example.mediamusicplayer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.mediamusicplayer to javafx.fxml;
    exports org.example.mediamusicplayer;
    exports org.example.mediamusicplayer.controller;
    opens org.example.mediamusicplayer.controller to javafx.fxml;



        // Permesso per far leggere le Canzoni (Track) alla Tabella
        opens org.example.mediamusicplayer.model to javafx.base;
        exports org.example.mediamusicplayer.model;
        }