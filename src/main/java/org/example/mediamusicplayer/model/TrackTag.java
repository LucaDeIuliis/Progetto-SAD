package org.example.mediamusicplayer.model;

public enum TrackTag {
    FAVOURITE("❤"),
    EXPLICIT("🔞"),
    NEW_RELEASE("✨");

    private final String symbol;

    TrackTag(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
