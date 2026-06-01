## Media Music Player 🎶

Applicazione desktop sviluppata in Java per la gestione di tracce musicali e playlist.  
Il progetto è stato realizzato come **Project Work** per il corso di **Software Architecture Design**, seguendo un processo di sviluppo ispirato a **Scrum**.

## Obiettivo del progetto

L’obiettivo finale è realizzare un’app desktop Java per creare, organizzare, visualizzare e gestire tracce e playlist musicali, con una simulazione della riproduzione e funzionalità progressivamente più avanzate come filtri, tag, modalità di playback e playlist automatiche.

La prima versione del progetto si concentra sulle funzionalità selezionate per il **primo sprint**, senza puntare a un'interfaccia grafica definitiva, ma a una UI sufficiente per supportare le User Story implementate.

## Funzionalità implementate nello Sprint 1

Le User Story considerate per il primo sprint sono:

| ID | User Story | Descrizione sintetica |
|---|---|---|
| US-01 | Creazione traccia | Permette all'utente di inserire una nuova traccia musicale. |
| US-02 | Visualizzazione tracce | Mostra l'elenco delle tracce presenti nel sistema. |
| US-04 | Eliminazione traccia | Permette di rimuovere una traccia dall'elenco. |
| US-05 | Creazione playlist | Permette all'utente di creare una nuova playlist. |
| US-08 | Visualizzazione playlist | Mostra le playlist create. |
| US-10 | Eliminazione playlist | Permette di eliminare una playlist esistente. |

## Tecnologie utilizzate

- **Java**
- **JavaFX** per l'interfaccia grafica desktop
- **FXML** per la separazione tra struttura grafica e logica di controllo
- **Git / GitHub** per il versionamento del codice
- **Trello** per la gestione delle User Story e delle attività dello sprint

## Architettura del progetto

Il progetto segue una struttura ispirata al pattern **MVC**:

- **Model**: contiene le classi che rappresentano il dominio dell'applicazione, ad esempio `Track` e `Playlist`.
- **View**: contiene i file FXML e gli elementi grafici dell'interfaccia utente.
- **Controller**: gestisce l'interazione tra interfaccia grafica e modello.

Una possibile organizzazione dei package è la seguente:

```text
src/main/java/org/example/mediamusicplayer/
│
├── controller/
│   ├── MusicPlayerController.java
│   └── PlaylistController.java
│
├── model/
│   ├── Track.java
│   └── Playlist.java
│
├── service/
│   ├── TrackService.java
│   └── PlaylistService.java
│
├── validator/
│   ├── TrackValidator.java
│   └── PlaylistValidator.java
│
└── Main.java
```

```text
src/main/resources/org/example/mediamusicplayer/
│
├── music-player-view.fxml
└── playlist-view.fxml
```

## Scelte progettuali

### Separazione delle responsabilità

Il progetto cerca di mantenere separate le diverse responsabilità:

- i **model** rappresentano i dati dell'applicazione;
- i **controller** gestiscono gli eventi dell'interfaccia grafica;
- i **service** contengono la logica applicativa principale;
- i **validator** si occupano del controllo dei dati inseriti dall'utente.

Questa separazione rende il codice più leggibile, manutenibile e più semplice da testare.

## Esecuzione del progetto

Per eseguire il progetto:

1. Clonare la repository:

```bash
git clone <URL_REPOSITORY>
```

2. Aprire il progetto con l'IDE scelto, ad esempio IntelliJ IDEA o Visual Studio Code.

3. Verificare che Java e JavaFX siano configurati correttamente.

4. Eseguire la classe principale:

```text
Main.java
```

## Gestione del progetto con Scrum

Il lavoro è organizzato secondo un processo Scrum semplificato.

### Product Backlog

Il Product Backlog contiene tutte le User Story previste per l'applicazione.

### Definition of Done

Una User Story può essere considerata completata quando:

- il codice relativo alla funzionalità è stato implementato;
- l'interfaccia grafica consente di usare la funzionalità prevista;
- gli input principali vengono validati;
- il codice è organizzato nei package corretti;
- non sono presenti errori bloccanti durante l'esecuzione;
- la funzionalità è stata verificata manualmente;
- il codice è stato caricato correttamente su GitHub.


## Autori

Project Work realizzato per il corso di **Software Architecture Design** da:

- Franchetti Carlo Maria
- Di Crescenzo Francesco
- Galluzzo Nicola Alessandro
- De Iulis Luca

