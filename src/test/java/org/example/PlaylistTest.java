package org.example;

import org.example.mediamusicplayer.model.Playlist;
import org.example.mediamusicplayer.model.Track;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
Questi test coprono:
- creazione playlist con ID automatico
- creazione playlist con costruttore completo
- modifica del nome
- aggiunta di tracce valide
- mancata aggiunta di tracce nulle
- rimozione di tracce presenti e assenti
- protezione della lista interna tramite copia
- riordinamento delle tracce
- gestione degli indici non validi
- size()
- isEmpty()
- toString()
*/

class PlaylistTest {

    private Track creaTraccia(String titolo, String autore) {
        return new Track(
                titolo,
                autore,
                Duration.ofMinutes(3).plusSeconds(30),
                "Pop italiano",
                Year.of(2024)
        );
    }

    @Test
    void costruttoreConNomeDovrebbeInizializzarePlaylistVuotaConIdValido() {
        Playlist playlist = new Playlist("Playlist preferita");

        assertNotNull(playlist.getId());
        assertFalse(playlist.getId().isBlank());
        assertEquals("Playlist preferita", playlist.getName());
        assertTrue(playlist.isEmpty());
        assertEquals(0, playlist.size());
        assertTrue(playlist.getTracks().isEmpty());
    }

    @Test
    void costruttoreCompletoDovrebbeInizializzareTuttiICampi() {
        Track primaTraccia = creaTraccia("La cura", "Franco Battiato");
        Track secondaTraccia = creaTraccia("Azzurro", "Adriano Celentano");

        List<Track> tracce = List.of(primaTraccia, secondaTraccia);

        Playlist playlist = new Playlist(
                "playlist-001",
                "Classici italiani",
                tracce
        );

        assertEquals("playlist-001", playlist.getId());
        assertEquals("Classici italiani", playlist.getName());
        assertEquals(2, playlist.size());
        assertFalse(playlist.isEmpty());
        assertEquals(primaTraccia, playlist.getTracks().get(0));
        assertEquals(secondaTraccia, playlist.getTracks().get(1));
    }

    @Test
    void setNameDovrebbeModificareNomePlaylist() {
        Playlist playlist = new Playlist("Nome iniziale");

        playlist.setName("Nome modificato");

        assertEquals("Nome modificato", playlist.getName());
    }

    @Test
    void addTrackDovrebbeAggiungereUnaTracciaValida() {
        Playlist playlist = new Playlist("Playlist pop");
        Track track = creaTraccia("Musica leggerissima", "Colapesce Dimartino");

        playlist.addTrack(track);

        assertEquals(1, playlist.size());
        assertFalse(playlist.isEmpty());
        assertEquals(track, playlist.getTracks().get(0));
    }

    @Test
    void addTrackNonDovrebbeAggiungereTracciaNulla() {
        Playlist playlist = new Playlist("Playlist vuota");

        playlist.addTrack(null);

        assertEquals(0, playlist.size());
        assertTrue(playlist.isEmpty());
    }

    @Test
    void removeTrackDovrebbeRimuovereUnaTracciaEsistente() {
        Playlist playlist = new Playlist("Playlist italiana");
        Track track = creaTraccia("Albachiara", "Vasco Rossi");

        playlist.addTrack(track);

        boolean risultato = playlist.removeTrack(track);

        assertTrue(risultato);
        assertEquals(0, playlist.size());
        assertTrue(playlist.isEmpty());
    }

    @Test
    void removeTrackDovrebbeRestituireFalseSeLaTracciaNonEsiste() {
        Playlist playlist = new Playlist("Playlist italiana");
        Track trackPresente = creaTraccia("Centro di gravità permanente", "Franco Battiato");
        Track trackAssente = creaTraccia("Senza fine", "Gino Paoli");

        playlist.addTrack(trackPresente);

        boolean risultato = playlist.removeTrack(trackAssente);

        assertFalse(risultato);
        assertEquals(1, playlist.size());
        assertEquals(trackPresente, playlist.getTracks().get(0));
    }

    @Test
    void getTracksDovrebbeRestituireUnaCopiaDellaListaInterna() {
        Playlist playlist = new Playlist("Playlist protetta");
        Track track = creaTraccia("Meraviglioso", "Domenico Modugno");

        playlist.addTrack(track);

        List<Track> tracceRestituite = playlist.getTracks();
        tracceRestituite.clear();

        assertEquals(1, playlist.size());
        assertEquals(track, playlist.getTracks().get(0));
    }

    @Test
    void costruttoreCompletoDovrebbeCopiareLaListaRicevuta() {
        Track primaTraccia = creaTraccia("Il cielo in una stanza", "Gino Paoli");
        Track secondaTraccia = creaTraccia("Caruso", "Lucio Dalla");

        List<Track> tracceOriginali = new ArrayList<>();
        tracceOriginali.add(primaTraccia);
        tracceOriginali.add(secondaTraccia);

        Playlist playlist = new Playlist(
                "playlist-002",
                "Cantautori italiani",
                tracceOriginali
        );

        tracceOriginali.clear();

        assertEquals(2, playlist.size());
        assertEquals(primaTraccia, playlist.getTracks().get(0));
        assertEquals(secondaTraccia, playlist.getTracks().get(1));
    }

    @Test
    void reorderTrackDovrebbeScambiareDueTracceValide() {
        Playlist playlist = new Playlist("Playlist ordinabile");

        Track primaTraccia = creaTraccia("Prima traccia", "Primo autore");
        Track secondaTraccia = creaTraccia("Seconda traccia", "Secondo autore");
        Track terzaTraccia = creaTraccia("Terza traccia", "Terzo autore");

        playlist.addTrack(primaTraccia);
        playlist.addTrack(secondaTraccia);
        playlist.addTrack(terzaTraccia);

        playlist.reorderTrack(0, 2);

        assertEquals(terzaTraccia, playlist.getTracks().get(0));
        assertEquals(secondaTraccia, playlist.getTracks().get(1));
        assertEquals(primaTraccia, playlist.getTracks().get(2));
    }

    @Test
    void reorderTrackDovrebbeLanciareEccezioneSeIndiceDiPartenzaNonValido() {
        Playlist playlist = new Playlist("Playlist ordinabile");
        playlist.addTrack(creaTraccia("Una canzone", "Un autore"));

        IndexOutOfBoundsException exception = assertThrows(
                IndexOutOfBoundsException.class,
                () -> playlist.reorderTrack(-1, 0)
        );

        assertEquals("Indice non valido", exception.getMessage());
    }

    @Test
    void reorderTrackDovrebbeLanciareEccezioneSeIndiceDiDestinazioneNonValido() {
        Playlist playlist = new Playlist("Playlist ordinabile");
        playlist.addTrack(creaTraccia("Una canzone", "Un autore"));

        IndexOutOfBoundsException exception = assertThrows(
                IndexOutOfBoundsException.class,
                () -> playlist.reorderTrack(0, 1)
        );

        assertEquals("Indice non valido", exception.getMessage());
    }

    @Test
    void sizeDovrebbeRestituireNumeroDiTraccePresenti() {
        Playlist playlist = new Playlist("Playlist dimensione");

        playlist.addTrack(creaTraccia("Prima canzone", "Primo autore"));
        playlist.addTrack(creaTraccia("Seconda canzone", "Secondo autore"));

        assertEquals(2, playlist.size());
    }

    @Test
    void isEmptyDovrebbeRestituireTrueQuandoPlaylistNonHaTracce() {
        Playlist playlist = new Playlist("Playlist vuota");

        assertTrue(playlist.isEmpty());
    }

    @Test
    void isEmptyDovrebbeRestituireFalseQuandoPlaylistHaAlmenoUnaTraccia() {
        Playlist playlist = new Playlist("Playlist non vuota");

        playlist.addTrack(creaTraccia("Una canzone italiana", "Un artista italiano"));

        assertFalse(playlist.isEmpty());
    }

    @Test
    void toStringDovrebbeRestituireRappresentazioneTestualeDellaPlaylist() {
        Playlist playlist = new Playlist("Estate italiana");

        playlist.addTrack(creaTraccia("Sapore di sale", "Gino Paoli"));
        playlist.addTrack(creaTraccia("Abbronzatissima", "Edoardo Vianello"));

        assertEquals("Estate italiana (2 tracks)", playlist.toString());
    }
}