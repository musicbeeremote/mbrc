package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.enums.PlayState;

public class NotificationDataAvailable {
    private String artist;
    private String title;
    private String album;
    private PlayState state;

    public NotificationDataAvailable(String artist, String title, String album, PlayState state) {
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.state = state;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public PlayState getState() {
        return state;
    }

    public String getAlbum() {
        return album;
    }
}
