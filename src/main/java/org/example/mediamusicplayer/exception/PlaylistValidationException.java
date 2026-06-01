package org.example.mediamusicplayer.exception;

public class PlaylistValidationException extends RuntimeException {
    private final String header;

    public PlaylistValidationException(String header, String message) {
        super(message);
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}