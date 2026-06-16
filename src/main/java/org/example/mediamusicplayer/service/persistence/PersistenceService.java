package org.example.mediamusicplayer.service.persistence;

import javafx.concurrent.Task;
import org.example.mediamusicplayer.model.MusicLibrary;
import org.example.mediamusicplayer.repository.MusicLibraryRepository;
import org.example.mediamusicplayer.repository.PlaylistRepository;
import org.example.mediamusicplayer.repository.TrackRepository;

public class PersistenceService {

    private final MusicLibraryRepository musicLibraryRepository;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    public PersistenceService(MusicLibraryRepository musicLibraryRepository, TrackRepository trackRepository, PlaylistRepository playlistRepository) {
        this.musicLibraryRepository = musicLibraryRepository;
        this.trackRepository = trackRepository;
        this.playlistRepository = playlistRepository;
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

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                musicLibraryRepository.save(library);
                return null;
            }
        };

        Thread saveThread = new Thread(saveTask);
        saveThread.setDaemon(true);
        saveThread.start();
    }

    public void deleteTrackAsync(String trackId) {
        if (trackId == null) {
            return;
        }

        Task<Void> deleteTask = new Task<Void>() {
            @Override
            protected Void call() {
                trackRepository.deleteById(trackId);
                return null;
            }
        };

        Thread deleteThread = new Thread(deleteTask);
        deleteThread.setDaemon(true);
        deleteThread.start();
    }

    public void deletePlaylistAsync(String playlistId) {
        if (playlistId == null) {
            return;
        }

        Task<Void> deleteTask = new Task<Void>() {
            @Override
            protected Void call() {
                playlistRepository.deleteById(playlistId);
                return null;
            }
        };

        Thread deleteThread = new Thread(deleteTask);
        deleteThread.setDaemon(true);
        deleteThread.start();
    }
}