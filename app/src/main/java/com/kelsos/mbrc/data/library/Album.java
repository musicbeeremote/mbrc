package com.kelsos.mbrc.data.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.kelsos.mbrc.data.db.CacheDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(name = "album", database = CacheDatabase.class)
public class Album extends BaseModel {
  @JsonIgnore
  @Column
  @PrimaryKey(autoincrement = true)
  private long id;

  @JsonProperty("artist")
  @Column
  private String artist;

  @JsonProperty("album")
  @Column
  private String album;

  @JsonProperty("count")
  @Column
  private int count;

  public Album() {

  }

  public Album(JsonNode node) {
    this.artist = node.path("artist").textValue();
    this.album = node.path("album").textValue();
    this.count = node.path("count").intValue();
  }

  @JsonProperty("artist")
  public String getArtist() {
    return artist;
  }

  @JsonProperty("artist")
  public void setArtist(String artist) {
    this.artist = artist;
  }

  @JsonProperty("album")
  public String getAlbum() {
    return album;
  }

  @JsonProperty("album")
  public void setAlbum(String album) {
    this.album = album;
  }

  @SuppressWarnings("unused")
  @JsonProperty("count")
  public int getCount() {
    return count;
  }

  @JsonProperty("count")
  public void setCount(int count) {
    this.count = count;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
