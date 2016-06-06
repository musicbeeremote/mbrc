package com.kelsos.mbrc.data.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "artist",
    "title",
    "src",
    "trackno",
    "disc"
})
@Table(name = "track", database = Cache.class)
public class Track extends BaseModel {

  @JsonIgnore
  @Column
  @PrimaryKey(autoincrement = true)
  private long id;
  @JsonProperty("artist")
  @Column
  private String artist;
  @JsonProperty("title")
  @Column
  private String title;
  @JsonProperty("src")
  @Column
  private String src;
  @JsonProperty("trackno")
  @Column
  private int trackno;
  @JsonProperty("disc")
  @Column
  private int disc;
  @JsonProperty("album_artist")
  @Column(name = "album_artist")
  private String albumArtist;
  @JsonProperty("album")
  @Column
  private String album;

  @JsonProperty("genre")
  @Column
  private String genre;

  public Track(JsonNode jNode) {
    this.artist = jNode.path("artist").textValue();
    this.title = jNode.path("title").textValue();
    this.src = jNode.path("src").textValue();
  }

  public Track() {

  }

  @JsonProperty("album_artist")
  public String getAlbumArtist() {
    return albumArtist;
  }

  @JsonProperty("album_artist")
  public void setAlbumArtist(String albumArtist) {
    this.albumArtist = albumArtist;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

  @JsonProperty("src")
  public String getSrc() {
    return src;
  }

  @JsonProperty("src")
  public void setSrc(String src) {
    this.src = src;
  }

  @JsonProperty("trackno")
  public int getTrackno() {
    return trackno;
  }

  @JsonProperty("trackno")
  public void setTrackno(int trackno) {
    this.trackno = trackno;
  }

  @JsonProperty("disc")
  public int getDisc() {
    return disc;
  }

  @JsonProperty("disc")
  public void setDisc(int disc) {
    this.disc = disc;
  }

  @JsonProperty("album")
  public String getAlbum() {
    return album;
  }

  @JsonProperty("album")
  public void setAlbum(String album) {
    this.album = album;
  }

  @JsonProperty("genre") public String getGenre() {
    return genre;
  }

  @JsonProperty("genre") public void setGenre(String genre) {
    this.genre = genre;
  }
}
