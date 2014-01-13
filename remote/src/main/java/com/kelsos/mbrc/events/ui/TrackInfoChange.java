package com.kelsos.mbrc.events.ui;

public class TrackInfoChange {
    private String artist;
    private String title;
    private String album;
    private String year;

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

    public TrackInfoChange(String artist, String title, String album, String year)  {
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.year = year;
    }


}
