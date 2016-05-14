package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;

public class RemoteClientMetaData {
  private final String artist;
  private final String title;
  private final String album;
  private final Bitmap cover;

  public RemoteClientMetaData(String artist, String title, String album, Bitmap cover) {
    this.artist = artist;
    this.title = title;
    this.album = album;
    this.cover = cover;
  }

  public String getArtist() {
    return artist;
  }

  public String getTitle() {
    return title;
  }

  public String getAlbum() {
    return album;
  }

  public Bitmap getCover() {
    return cover;
  }
}
