package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = RemoteDatabase.class, name = "artists") public class ArtistDao extends BaseModel {

  @Column @PrimaryKey(autoincrement = true)private long id;
  @Column private String name;
  @Column(name = "date_added") private long dateAdded;
  @Column(name = "date_updated") private long dateUpdated;
  @Column(name = "date_deleted") private long dateDeleted;

  public String getName() {
    return name;
  }

  public ArtistDao setName(String name) {
    this.name = name;
    return this;
  }

  public long getId() {
    return id;
  }

  public ArtistDao setId(long id) {
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
}
