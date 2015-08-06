package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME) public class Album extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private String name;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "artist_id",
              columnType = Long.class,
              foreignColumnName = "id")
      }, saveForeignKeyModel = false) private Artist artist;

  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "cover_id",
              columnType = Long.class,
              foreignColumnName = "id")
      }, saveForeignKeyModel = false) private Cover cover;

  public long getId() {
    return id;
  }

  public Album setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Album setName(String name) {
    this.name = name;
    return this;
  }

  public Artist getArtist() {
    return artist;
  }

  public Album setArtist(Artist artist) {
    this.artist = artist;
    return this;
  }

  public Cover getCover() {
    return cover;
  }

  public Album setCover(Cover cover) {
    this.cover = cover;
    return this;
  }
}
