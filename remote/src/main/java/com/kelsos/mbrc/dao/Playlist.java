package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME) public class Playlist extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private String name;
  @Column private boolean readOnly;
  @Column private String path;
  @Column private int tracks;

  public long getId() {
    return id;
  }

  public Playlist setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Playlist setName(String name) {
    this.name = name;
    return this;
  }

  public boolean getReadOnly() {
    return readOnly;
  }

  public Playlist setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  public String getPath() {
    return path;
  }

  public Playlist setPath(String path) {
    this.path = path;
    return this;
  }

  public int getTracks() {
    return tracks;
  }

  public Playlist setTracks(int tracks) {
    this.tracks = tracks;
    return this;
  }
}
