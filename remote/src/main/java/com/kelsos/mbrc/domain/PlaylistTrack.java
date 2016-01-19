package com.kelsos.mbrc.domain;

public class PlaylistTrack {
  private long id;
  private String artist;
  private String title;
  private int position;

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

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }
}
