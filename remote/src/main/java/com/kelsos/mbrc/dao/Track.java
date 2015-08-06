package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME) public class Track extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private String title;
  @Column private int position;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "genre_id",
              columnType = Long.class,
              foreignColumnName = "id")
      }, saveForeignKeyModel = false)
  private Genre genre;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "artist_id",
              columnType = Long.class,
              foreignColumnName = "id")
      }, saveForeignKeyModel = false) private Artist artist;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "album_artist_id",
              columnType = Long.class,
              foreignColumnName = "id")
      }, saveForeignKeyModel = false)
  private Artist albumArtist;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "album_id",
              columnType = Long.class,
              foreignColumnName = "id")
      }, saveForeignKeyModel = false)
  private Album album;
  @Column private String year;
  @Column private String path;

  public String getPath() {
    return path;
  }

  public Track setPath(String path) {
    this.path = path;
    return this;
  }

  public String getYear() {
    return year;
  }

  public Track setYear(String year) {
    this.year = year;
    return this;
  }

  public Album getAlbum() {
    return album;
  }

  public Track setAlbum(Album album) {
    this.album = album;
    return this;
  }

  public Artist getAlbumArtist() {
    return albumArtist;
  }

  public Track setAlbumArtist(Artist albumArtist) {
    this.albumArtist = albumArtist;
    return this;
  }

  public Artist getArtist() {
    return artist;
  }

  public Track setArtist(Artist artist) {
    this.artist = artist;
    return this;
  }

  public Genre getGenre() {
    return genre;
  }

  public Track setGenre(Genre genre) {
    this.genre = genre;
    return this;
  }

  public int getPosition() {
    return position;
  }

  public Track setPosition(int position) {
    this.position = position;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public Track setTitle(String title) {
    this.title = title;
    return this;
  }

  public long getId() {
    return id;
  }

  public Track setId(long id) {
    this.id = id;
    return this;
  }
}
