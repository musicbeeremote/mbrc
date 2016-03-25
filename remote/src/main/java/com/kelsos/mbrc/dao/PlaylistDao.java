package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = RemoteDatabase.class, name = "playlists") public class PlaylistDao extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) private long id;
  @Column @Index private String name;
  @Column(name = "read_only") private boolean readOnly;
  @Column private String path;
  @Column private int tracks;
  @Column(name = "date_added") private long dateAdded;
  @Column(name = "date_updated") private long dateUpdated;
  @Column(name = "date_deleted") private long dateDeleted;

  public long getId() {
    return id;
  }

  public PlaylistDao setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public PlaylistDao setName(String name) {
    this.name = name;
    return this;
  }

  public boolean getReadOnly() {
    return readOnly;
  }

  public String getPath() {
    return path;
  }

  public PlaylistDao setPath(String path) {
    this.path = path;
    return this;
  }

  public int getTracks() {
    return tracks;
  }

  public PlaylistDao setTracks(int tracks) {
    this.tracks = tracks;
    return this;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public PlaylistDao setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  public long getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }

  public long getDateUpdated() {
    return dateUpdated;
  }

  public void setDateUpdated(long dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  public long getDateDeleted() {
    return dateDeleted;
  }

  public void setDateDeleted(long dateDeleted) {
    this.dateDeleted = dateDeleted;
  }
}
