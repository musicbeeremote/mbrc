package com.kelsos.mbrc.rest.responses;

public class TrackInfo {
  private String artist;
  private String title;
  private String album;
  private String year;
  private String path;

  @SuppressWarnings("UnusedDeclaration")
  public TrackInfo(String artist, String title, String album, String year, String path) {
    this.artist = artist;
    this.title = title;
    this.album = album;
    this.year = year;
    this.path = path;
  }

  @SuppressWarnings("UnusedDeclaration") public TrackInfo() { }

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
