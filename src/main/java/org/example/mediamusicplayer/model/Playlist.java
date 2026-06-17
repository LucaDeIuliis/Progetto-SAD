package org.example.mediamusicplayer.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Map;
import java.util.UUID;

public class Playlist {
    private String id;
    private String name;
    private ObservableList<Track> tracks;
    private boolean generataAutomaticamente;

    // "Genere" oppure "Anno"
    private String tipoFiltro;

    // Es. "Rock" oppure "2024"
    private String filtroAutomatico;

    private int playCount;

    public Playlist(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.tracks = FXCollections.observableArrayList();
        this.generataAutomaticamente = false;
        this.playCount = 0;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void incrementPlayCount() {
        playCount++;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public ObservableList<Track> getTracks() {
        return tracks;
    }


    public void addTrack(Track track) {
        this.tracks.add(track);
    }


    public void removeTrack(Track track) {
        this.tracks.remove(track);
    }


    public boolean isGenerataAutomaticamente() {
        return generataAutomaticamente;
    }


    public void setGenerataAutomaticamente(boolean valore) {
        this.generataAutomaticamente = valore;
    }


    public String getTipoFiltro() {
        return tipoFiltro;
    }


    public void setTipoFiltro(String tipoFiltro) {
        this.tipoFiltro = tipoFiltro;
    }


    public String getFiltroAutomatico() {
        return filtroAutomatico;
    }

    public void setPlayCount(int playCount) { this.playCount = playCount; }

    public void setFiltroAutomatico(String filtroAutomatico) {
        this.filtroAutomatico = filtroAutomatico;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Playlist copy(Map<String, Track> copiedTracksById) {
        Playlist copy = new Playlist(this.name);

        copy.setId(this.id);
        copy.setGenerataAutomaticamente(this.generataAutomaticamente);
        copy.setTipoFiltro(this.tipoFiltro);
        copy.setFiltroAutomatico(this.filtroAutomatico);
        copy.setPlayCount(this.playCount);

        for (Track originalTrack : this.tracks) {
            Track copiedTrack = copiedTracksById.get(originalTrack.getId());

            if (copiedTrack != null) {
                copy.getTracks().add(copiedTrack);
            }
        }

        return copy;
    }

    @Override
    public String toString() {
        return name;
    }
}