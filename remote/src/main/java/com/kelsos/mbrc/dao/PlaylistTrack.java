package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME) public class PlaylistTrack extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private int position;
  @Column private String path;
  @Column private String artist;
  @Column private String title;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "playlist_id",
              columnType = Long.class,
              foreignColumnName = "id")
      }, saveForeignKeyModel = false) private PlaylistDao playlist;

  public PlaylistDao getPlaylist() {
    return playlist;
  }

  public PlaylistTrack setPlaylist(PlaylistDao playlist) {
    this.playlist = playlist;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public PlaylistTrack setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getArtist() {
    return artist;
  }

  public PlaylistTrack setArtist(String artist) {
    this.artist = artist;
    return this;
  }

  public String getPath() {
    return path;
  }

  public PlaylistTrack setPath(String path) {
    this.path = path;
    return this;
  }

  public int getPosition() {
    return position;
  }

  public PlaylistTrack setPosition(int position) {
    this.position = position;
    return this;
  }

  public long getId() {
    return id;
  }

  public PlaylistTrack setId(long id) {
    this.id = id;
    return this;
  }
}
