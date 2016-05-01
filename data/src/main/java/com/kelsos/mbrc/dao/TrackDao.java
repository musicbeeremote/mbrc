package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.IndexGroup;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = RemoteDatabase.class, name = "tracks",
    indexGroups = {
        @IndexGroup(number = 1, name = "track_title_index")
    })
public class TrackDao extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) private long id;
  @Column @Index(indexGroups = 1) private String title;
  @Column private int position;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "genre_id",
              columnType = long.class,
              referencedFieldIsPrivate = true,
              foreignKeyColumnName = "id")
      }, saveForeignKeyModel = false) private GenreDao genre;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "artist_id",
              columnType = long.class,
              referencedFieldIsPrivate = true,
              foreignKeyColumnName = "id")
      }, saveForeignKeyModel = false) private ArtistDao artist;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "album_artist_id",
              columnType = long.class,
              referencedFieldIsPrivate = true,
              foreignKeyColumnName = "id")
      }, saveForeignKeyModel = false) private ArtistDao albumArtist;
  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "album_id",
              columnType = long.class,
              referencedFieldIsPrivate = true,
              foreignKeyColumnName = "id")
      }, saveForeignKeyModel = false) private AlbumDao album;
  @Column private String year;
  @Column private String path;
  @Column(name = "date_added") private long dateAdded;
  @Column(name = "date_updated") private long dateUpdated;
  @Column(name = "date_deleted") private long dateDeleted;
  @Column private int disc;

  public String getPath() {
    return path;
  }

  public TrackDao setPath(String path) {
    this.path = path;
    return this;
  }

  public String getYear() {
    return year;
  }

  public TrackDao setYear(String year) {
    this.year = year;
    return this;
  }

  public AlbumDao getAlbum() {
    return album;
  }

  public TrackDao setAlbum(AlbumDao album) {
    this.album = album;
    return this;
  }

  public ArtistDao getAlbumArtist() {
    return albumArtist;
  }

  public TrackDao setAlbumArtist(ArtistDao albumArtist) {
    this.albumArtist = albumArtist;
    return this;
  }

  public ArtistDao getArtist() {
    return artist;
  }

  public TrackDao setArtist(ArtistDao artist) {
    this.artist = artist;
    return this;
  }

  public GenreDao getGenre() {
    return genre;
  }

  public TrackDao setGenre(GenreDao genre) {
    this.genre = genre;
    return this;
  }

  public int getPosition() {
    return position;
  }

  public TrackDao setPosition(int position) {
    this.position = position;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public TrackDao setTitle(String title) {
    this.title = title;
    return this;
  }

  public long getId() {
    return id;
  }

  public TrackDao setId(long id) {
    this.id = id;
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

  public void setDisc(int disc) {
    this.disc = disc;
  }

  public int getDisc() {
    return disc;
  }
}
