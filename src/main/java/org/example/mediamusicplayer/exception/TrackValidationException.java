package org.example.mediamusicplayer.exception;

public class TrackValidationException extends RuntimeException {
    private final String header;

    public TrackValidationException(String header, String message) {
        super(message); // Il messaggio dell'errore
        this.header = header; // Il titolo del pop-up
    }

    public String getHeader() {
        return header;
    }
}