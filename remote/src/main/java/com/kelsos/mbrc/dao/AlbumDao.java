package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = RemoteDatabase.class, name = "albums") public class AlbumDao extends BaseModel {
  @PrimaryKey(autoincrement = true) private long id;
  @Column(name = "name") @Index private String name;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "artist_id",
              columnType = long.class,
              referencedFieldIsPrivate = true,
              foreignKeyColumnName = "id")
      }, saveForeignKeyModel = false) private ArtistDao artist;

  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "cover_id",
              columnType = long.class,
              referencedFieldIsPrivate = true,
              foreignKeyColumnName = "id")
      }, saveForeignKeyModel = false) private CoverDao cover;

  @Column(name = "date_added") private long dateAdded;
  @Column(name = "date_updated") private long dateUpdated;
  @Column(name = "date_deleted") private long dateDeleted;

  public long getId() {
    return id;
  }

  public AlbumDao setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public AlbumDao setName(String name) {
    this.name = name;
    return this;
  }

  public ArtistDao getArtist() {
    return artist;
  }

  public AlbumDao setArtist(ArtistDao artist) {
    this.artist = artist;
    return this;
  }

  public CoverDao getCover() {
    return cover;
  }

  public AlbumDao setCover(CoverDao cover) {
    this.cover = cover;
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
