
package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.data.library.Cache;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

@JsonPropertyOrder({
    "artist",
    "title",
    "path",
    "position"
})
@Table(database = Cache.class, name = "now_playing")
public class NowPlaying extends BaseModel {

  @JsonIgnore
  @PrimaryKey(autoincrement = true)
  private long id;

  @JsonProperty("artist")
  @Column
  private String artist;

  @JsonProperty("title")
  @Column
  private String title;

  @JsonProperty("path")
  @Column
  private String path;

  @JsonProperty("position")
  @Column
  private Integer position;

  public NowPlaying(String artist, String title) {
    this.artist = artist;
    this.title = title;
  }

  public NowPlaying() {

  }

  @JsonProperty("artist")
  public String getArtist() {
    return artist;
  }

  @JsonProperty("artist")
  public void setArtist(String artist) {
    this.artist = artist;
  }

  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty("path")
  public String getPath() {
    return path;
  }

  @JsonProperty("path")
  public void setPath(String path) {
    this.path = path;
  }

  @JsonProperty("position")
  public Integer getPosition() {
    return position;
  }

  @JsonProperty("position")
  public void setPosition(Integer position) {
    this.position = position;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}
