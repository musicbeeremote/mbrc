package com.kelsos.mbrc.domain;

public class Track {
  private long id;
  private String artist;
  private String title;
  private String cover;

  public Track(long id, String artist, String title, String cover) {
    this.id = id;
    this.artist = artist;
    this.title = title;
    this.cover = cover;
  }

  public long getId() {
    return id;
  }

  public String getArtist() {
    return artist;
  }

  public String getTitle() {
    return title;
  }

  public String getCover() {
    return cover;
  }
}
