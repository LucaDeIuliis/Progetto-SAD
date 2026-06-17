package org.example.mediamusicplayer.service.persistence;

import javafx.concurrent.Task;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.repository.MusicLibraryRepository;
import org.example.mediamusicplayer.repository.PlaylistRepository;
import org.example.mediamusicplayer.repository.TrackRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class PersistenceService {

    private final MusicLibraryRepository musicLibraryRepository;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;
    private final ExecutorService persistenceExecutor;

    public PersistenceService(
            MusicLibraryRepository musicLibraryRepository,
            TrackRepository trackRepository,
            PlaylistRepository playlistRepository
    ) {
        this.musicLibraryRepository = musicLibraryRepository;
        this.trackRepository = trackRepository;
        this.playlistRepository = playlistRepository;

        this.persistenceExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "persistence-thread");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void saveLibrary(MusicLibrary library) {
        if (library == null) {
            return;
        }

        musicLibraryRepository.save(library);
    }

    public void saveLibraryAsync(MusicLibrary library) {
        if (library == null) {
            return;
        }

        MusicLibrary librarySnapshot = library.copy();

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                musicLibraryRepository.save(librarySnapshot);
                return null;
            }
        };

        persistenceExecutor.execute(saveTask);
    }

    public void deleteTrackAsync(String trackId) {
        if (trackId == null) {
            return;
        }

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() {
                trackRepository.deleteById(trackId);
                return null;
            }
        };

        persistenceExecutor.execute(deleteTask);
    }

    public void deletePlaylistAsync(String playlistId) {
        if (playlistId == null) {
            return;
        }

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() {
                playlistRepository.deleteById(playlistId);
                return null;
            }
        };

        persistenceExecutor.execute(deleteTask);
    }

    public void loadLibraryAsync(
            Consumer<MusicLibrary> onSuccess,
            Consumer<Throwable> onError
    ) {
        Task<MusicLibrary> loadTask = new Task<>() {
            @Override
            protected MusicLibrary call() {
                return musicLibraryRepository.load();
            }
        };

        loadTask.setOnSucceeded(event -> {
            if (onSuccess != null) {
                onSuccess.accept(loadTask.getValue());
            }
        });

        loadTask.setOnFailed(event -> {
            if (onError != null) {
                onError.accept(loadTask.getException());
            }
        });

        persistenceExecutor.execute(loadTask);
    }

    public void shutdown() {
        persistenceExecutor.shutdown();

        try {
            if (!persistenceExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
                persistenceExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            persistenceExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}