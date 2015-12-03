package com.kelsos.mbrc.domain;

public class Playlist {
  private long id;
  private String name;
  private boolean readOnly;
  private String path;
  private int tracks;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getTracks() {
    return tracks;
  }

  public void setTracks(int tracks) {
    this.tracks = tracks;
  }
}
