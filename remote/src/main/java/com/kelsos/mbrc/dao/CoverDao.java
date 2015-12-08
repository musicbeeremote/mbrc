package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME,tableName = "covers") public class CoverDao extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private String hash;
  @Column(name = "date_added") private long dateAdded;
  @Column(name = "date_updated") private long dateUpdated;
  @Column(name = "date_deleted") private long dateDeleted;

  public String getHash() {
    return hash;
  }

  public CoverDao setHash(String hash) {
    this.hash = hash;
    return this;
  }

  public long getId() {
    return id;
  }

  public CoverDao setId(long id) {
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
