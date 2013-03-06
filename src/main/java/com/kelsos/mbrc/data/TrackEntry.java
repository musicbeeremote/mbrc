package com.kelsos.mbrc.data;

public class TrackEntry {
    private String artist;
    private String title;

    public TrackEntry(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}
