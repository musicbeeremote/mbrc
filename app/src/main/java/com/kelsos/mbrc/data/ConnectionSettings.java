package com.kelsos.mbrc.data;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kelsos.mbrc.data.db.CacheDatabase;
import com.kelsos.mbrc.data.db.SettingsDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(name = "settings", database = SettingsDatabase.class,
    uniqueColumnGroups = {
        @UniqueGroup(groupNumber = 1, uniqueConflict = ConflictAction.IGNORE)
})
public class ConnectionSettings extends BaseModel {
  @PrimaryKey(autoincrement = true)
  @Column(name = "id")
  private long id;
  @Column(name = "address")
  @Unique(unique = false, uniqueGroups = 1)
  private String address;
  @Column(name = "name")
  private String name;
  @Unique(unique = false, uniqueGroups = 1)
  @Column(name = "port")
  private int port;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getAddress() {
    return this.address;
  }

  public String getName() {
    return this.name;
  }

  public int getPort() {
    return this.port;
  }

}
