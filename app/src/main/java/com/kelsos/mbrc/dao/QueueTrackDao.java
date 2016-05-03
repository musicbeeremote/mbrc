package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = RemoteDatabase.class, name = "queue_tracks")
public class QueueTrackDao extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) private long id;
  @Column private String artist;
  @Column private String title;
  @Column private String path;
  @Column private int position;

  public int getPosition() {
    return position;
  }

  public QueueTrackDao setPosition(int position) {
    this.position = position;
    return this;
  }

  public String getPath() {
    return path;
  }

  public QueueTrackDao setPath(String path) {
    this.path = path;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public QueueTrackDao setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getArtist() {
    return artist;
  }

  public QueueTrackDao setArtist(String artist) {
    this.artist = artist;
    return this;
  }

  public long getId() {
    return id;
  }

  public QueueTrackDao setId(long id) {
    this.id = id;
    return this;
  }
}
