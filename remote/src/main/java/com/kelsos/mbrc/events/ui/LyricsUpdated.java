package com.kelsos.mbrc.events.ui;

public class LyricsUpdated {

    private String lyrics;

    public LyricsUpdated(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return this.lyrics;
    }
}
