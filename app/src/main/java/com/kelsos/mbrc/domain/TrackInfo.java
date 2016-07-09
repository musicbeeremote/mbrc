package com.kelsos.mbrc.domain;

public class TrackInfo {
  public String artist;
  public String title;
  public String album;
  public String year;

  public TrackInfo(String artist, String title, String album, String year) {
    this.artist = artist;
    this.title = title;
    this.album = album;
    this.year = year;
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
}
