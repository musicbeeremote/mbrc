package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = RemoteDatabase.class, name = "playlist_track") public class PlaylistTrackDao extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) private long id;
  @Column private int position;

  @Column @ForeignKey(
      references = {
          @ForeignKeyReference(columnName = "playlist_id",
              columnType = long.class,
              referencedFieldIsPrivate = true,
              foreignKeyColumnName = "id")
      }, saveForeignKeyModel = false) private PlaylistDao playlist;

  @Column @ForeignKey(references = {
      @ForeignKeyReference(columnName = "track_info_id",
          columnType = long.class,
          foreignKeyColumnName = "id",
          referencedFieldIsPrivate = true)
  }, saveForeignKeyModel = false) private PlaylistTrackInfoDao trackInfo;

  @Column(name = "date_added") private long dateAdded;
  @Column(name = "date_updated") private long dateUpdated;
  @Column(name = "date_deleted") private long dateDeleted;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public PlaylistDao getPlaylist() {
    return playlist;
  }

  public void setPlaylist(PlaylistDao playlist) {
    this.playlist = playlist;
  }

  public PlaylistTrackInfoDao getTrackInfo() {
    return trackInfo;
  }

  public void setTrackInfo(PlaylistTrackInfoDao trackInfo) {
    this.trackInfo = trackInfo;
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
