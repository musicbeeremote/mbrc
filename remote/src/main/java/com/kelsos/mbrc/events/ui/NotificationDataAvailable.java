package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;
import com.kelsos.mbrc.enums.PlayState;

public class NotificationDataAvailable {
    private String artist;
    private String title;
    private String album;
    private Bitmap cover;
    private PlayState state;

    public NotificationDataAvailable(String artist, String title, String album, Bitmap cover, PlayState state) {
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.cover = cover;
        this.state = state;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getCover() {
        return cover;
    }

    public PlayState getState() {
        return state;
    }

    public String getAlbum() {
        return album;
    }
}
