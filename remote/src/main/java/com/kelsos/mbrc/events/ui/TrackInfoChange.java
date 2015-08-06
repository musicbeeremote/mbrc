package com.kelsos.mbrc.events.ui;

public class TrackInfoChange {
  public String artist;
  public String title;
  public String album;
  public String year;
  private String path;

  public TrackInfoChange(String artist, String title, String album, String year, String path) {
    this.artist = artist;
    this.title = title;
    this.album = album;
    this.year = year;
    this.path = path;
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

  public String getYear() {
    return year;
  }

  public String getPath() {
    return path;
  }
}
