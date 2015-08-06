package com.kelsos.mbrc.dao;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(databaseName = RemoteDatabase.NAME) public class Artist extends BaseModel {

  @Column @PrimaryKey(autoincrement = true) long id;
  @Column private String name;

  public String getName() {
    return name;
  }

  public Artist setName(String name) {
    this.name = name;
    return this;
  }

  public long getId() {
    return id;
  }

  public Artist setId(long id) {
    this.id = id;
    return this;
  }
}
