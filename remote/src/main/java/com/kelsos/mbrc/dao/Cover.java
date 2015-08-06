package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME) public class Cover extends BaseModel {
  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private String hash;

  public String getHash() {
    return hash;
  }

  public Cover setHash(String hash) {
    this.hash = hash;
    return this;
  }

  public long getId() {
    return id;
  }

  public Cover setId(long id) {
    this.id = id;
    return this;
  }
}
