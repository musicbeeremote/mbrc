package com.kelsos.mbrc.domain;

public class PlaylistTrack {
  private long id;
  private String artist;
  private String title;
  private String path;
  private long position;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public long getPosition() {
    return position;
  }

  public void setPosition(long position) {
    this.position = position;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
