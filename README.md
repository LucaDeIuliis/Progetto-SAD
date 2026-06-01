# Media Music Player

Desktop application developed in Java for the **Software Architecture Design** project work.

The goal of the project is to implement a **Music Playlist Manager**, allowing users to create and manage tracks and playlists through a desktop graphical interface. The application is developed incrementally using the **Scrum** process, with features selected sprint by sprint from the Product Backlog.

---

## Project overview

**Media Music Player** is a Java desktop application for managing a simple music library.

The application currently focuses on the core features planned for the first sprint:

- create a new track;
- display the list of available tracks;
- delete an existing track;
- create a playlist;
- display the list of playlists;
- delete an existing playlist.

Future increments may include track editing, adding/removing tracks from playlists, simulated playback, playback modes, filters, automatic playlist generation and advanced visualization features.

---

## Main features

### Tracks

A track represents a song in the music library and contains the following information:

- title;
- author;
- length;
- genre;
- year of publication.

The user can create, view and delete tracks from the application interface.

### Playlists

A playlist is identified by a name and is used to organize tracks.

The user can create, view and delete playlists. Playlist management will be extended in later sprints with operations for adding and removing tracks.

---

## Sprint 1 scope

The first sprint is focused on the minimum usable version of the application, covering the following User Stories:

| ID | User Story |
|---|---|
| US-01 | Creazione Traccia |
| US-02 | Visualizzazione delle tracce |
| US-04 | Eliminazione di una Traccia |
| US-05 | Creazione di una playlist |
| US-08 | Visualizzazione playlist |
| US-10 | Eliminazione playlist |

The goal is not to deliver the final complete product, but to provide a working increment that compiles, can be tested and demonstrates the selected features.

---

## Technologies

- **Java**
- **JavaFX** for the desktop graphical interface
- **FXML** for UI layout definition
- **JUnit** for unit testing
- **Git/GitHub** for version control
- **Trello** for Scrum task tracking

---

## Suggested project structure

```text
src/
└── main/
    ├── java/
    │   └── org/example/mediamusicplayer/
    │       ├── Main.java
    │       ├── controller/
    │       │   └── ...
    │       ├── model/
    │       │   └── ...
    │       ├── service/
    │       │   └── ...
    │       └── validator/
    │           └── ...
    └── resources/
        └── org/example/mediamusicplayer/
            └── ...

src/
└── test/
    └── java/
        └── org/example/mediamusicplayer/
            └── ...
```

The structure separates responsibilities as follows:

- `model`: domain classes such as `Track` and `Playlist`;
- `controller`: JavaFX controllers that connect the UI to the application logic;
- `service`: application/business logic;
- `validator`: input validation classes;
- `resources`: FXML files and UI resources;
- `test`: automated unit tests.

---

## Architecture

The application follows a layered organization inspired by the MVC approach:

- the **Model** represents the domain entities of the application;
- the **View** is defined through JavaFX/FXML files;
- the **Controller** handles user interaction and coordinates the UI;
- the **Service** layer contains business operations and keeps the controller simpler;
- the **Validator** components check user input before creating or modifying domain objects.

This organization aims to improve cohesion, reduce coupling and keep the code easier to test and evolve across sprints.

---

## How to run the project

### Prerequisites

Make sure the following tools are installed:

- Java JDK 17 or later;
- Maven or Gradle, depending on the final project configuration;
- an IDE such as IntelliJ IDEA or Visual Studio Code with Java support.

### Run from IDE

1. Clone the repository.
2. Open the project in the IDE.
3. Let the IDE import dependencies.
4. Run the main JavaFX application class.

### Run from terminal

If the project uses Maven:

```bash
mvn clean javafx:run
```

If the project uses Gradle:

```bash
./gradlew run
```

Update this section according to the final build tool used by the project.

---

## How to run tests

If the project uses Maven:

```bash
mvn test
```

If the project uses Gradle:

```bash
./gradlew test
```

Unit tests should cover at least the public behavior of the business-logic classes and validation components.

---

## Scrum process

The project is developed using Scrum.

The repository is expected to contain or link the following artifacts:

- source code;
- unit tests;
- software architecture document;
- Product Backlog;
- Sprint Backlogs;
- Burndown Chart;
- Sprint Review Reports;
- Sprint Retrospective Reports;
- final presentation.

Trello is used as the Sprint Task Board to track tasks, assignments and time spent by each team member.

---

## Definition of Done

A User Story is considered done when:

- the feature is implemented and integrated in the application;
- the application compiles without errors;
- the feature can be executed from the graphical interface, when applicable;
- input validation is handled where necessary;
- relevant unit tests are added or updated;
- the code follows the agreed package structure and naming conventions;
- the implementation is committed to the GitHub repository;
- the corresponding Trello task is updated.

---

## Repository contents

```text
.
├── src/                         # Application source code
├── docs/                        # Project documentation and Scrum artifacts
├── README.md                    # Project overview and setup instructions
├── pom.xml / build.gradle       # Build configuration, if present
└── .gitignore                   # Git ignored files
```

---

## Team workflow

Recommended workflow:

1. choose a task from the Sprint Backlog;
2. create or update the related branch;
3. implement the feature in small, compilable steps;
4. add or update tests;
5. commit changes with clear messages;
6. open a pull request or merge after review;
7. update the Trello task status.

---

## Status

Project under development.

Current focus: first sprint release with the basic management of tracks and playlists.
