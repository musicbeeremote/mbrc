package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;
import com.kelsos.mbrc.enums.PlayState;

public class NotificationDataAvailable {
  private String artist;
  private String title;
  private String album;
  private PlayState state;
  private Bitmap cover;

  public NotificationDataAvailable(String artist, String title, String album, PlayState state,
      Bitmap cover) {
    this.artist = artist;
    this.title = title;
    this.album = album;
    this.state = state;
    this.cover = cover;
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

  public Bitmap getCover() {
    return cover;
  }
}
