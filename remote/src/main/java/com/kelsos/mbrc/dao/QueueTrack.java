package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME)
public class QueueTrack extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private String artist;
  @Column private String title;
  @Column private String path;
  @Column private int position;

  public int getPosition() {
    return position;
  }

  public QueueTrack setPosition(int position) {
    this.position = position;
    return this;
  }

  public String getPath() {
    return path;
  }

  public QueueTrack setPath(String path) {
    this.path = path;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public QueueTrack setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getArtist() {
    return artist;
  }

  public QueueTrack setArtist(String artist) {
    this.artist = artist;
    return this;
  }

  public long getId() {
    return id;
  }

  public QueueTrack setId(long id) {
    this.id = id;
    return this;
  }
}
