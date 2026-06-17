package org.example.mediamusicplayer.service.persistence;

import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.persistence.DatabaseManager;
import org.example.mediamusicplayer.repository.MusicLibraryRepository;
import org.example.mediamusicplayer.repository.PlaylistRepository;
import org.example.mediamusicplayer.repository.TrackRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;


class PersistenceServiceTest {


    private PersistenceService persistenceService;


    @AfterEach
    void tearDown() {

        if (persistenceService != null) {
            persistenceService.shutdown();
        }
    }


    @Test
    void saveLibrary_ShouldCallRepository() {

        AtomicBoolean saved = new AtomicBoolean(false);


        MusicLibraryRepository libraryRepository =
                new MusicLibraryRepository(
                        null,
                        null,
                        null
                ) {

                    @Override
                    public void save(MusicLibrary library)
                            throws SQLException {

                        saved.set(true);
                    }
                };


        TrackRepository trackRepository =
                null;

        PlaylistRepository playlistRepository =
                null;


        persistenceService =
                new PersistenceService(
                        libraryRepository,
                        trackRepository,
                        playlistRepository
                );


        persistenceService.saveLibrary(
                new MusicLibrary()
        );


        assertTrue(saved.get());
    }




    @Test
    void saveLibraryAsync_NullLibrary_ShouldNotSave()
            throws InterruptedException {


        AtomicBoolean saved =
                new AtomicBoolean(false);


        MusicLibraryRepository repository =
                new MusicLibraryRepository(
                        null,
                        null,
                        null
                ) {

                    @Override
                    public void save(MusicLibrary library)
                            throws SQLException {

                        saved.set(true);
                    }
                };


        persistenceService =
                new PersistenceService(
                        repository,
                        null,
                        null
                );


        persistenceService.saveLibraryAsync(null);


        Thread.sleep(200);


        assertFalse(saved.get());
    }












    @Test
    void shutdown_ShouldTerminateExecutor() {

        persistenceService =
                new PersistenceService(
                        null,
                        null,
                        null
                );


        assertDoesNotThrow(
                () -> persistenceService.shutdown()
        );
    }
}
