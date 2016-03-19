package com.kelsos.mbrc.domain;

import com.kelsos.mbrc.RemoteDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = RemoteDatabase.class, name = "settings",
    uniqueColumnGroups = {
        @UniqueGroup(groupNumber = 1, uniqueConflict = ConflictAction.REPLACE)
    }) public class DeviceSettings extends BaseModel {
  @Column(name = "id") @PrimaryKey(autoincrement = true) private long id;
  @Column(name = "address") @Unique(uniqueGroups = 1, unique = false) private String address;
  @Column(name = "name") private String name;
  @Column(name = "port") @Unique(uniqueGroups = 1, unique = false) private int port;
  @Column(name = "http") @Unique(uniqueGroups = 1, unique = false) private int http;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPort() {
    return this.port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getHttp() {
    return this.http;
  }

  public void setHttp(int http) {
    this.http = http;
  }

  @Override public boolean equals(Object o) {
    boolean equality = false;

    if (o instanceof DeviceSettings) {
      DeviceSettings other = (DeviceSettings) o;
      equality = other.getAddress().equals(address) && other.getPort() == port;
    }
    return equality;
  }

  @Override public int hashCode() {
    int hash = 0x192;
    hash = hash * 17 + port;
    hash = hash * 31 + address.hashCode();
    return hash;
  }
}
