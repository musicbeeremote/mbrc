package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Album {
  @JsonProperty("artist") private String artist;
  @JsonProperty("album") private String album;
  @JsonProperty("count") private int count;

  public Album() {

  }

  public Album(JsonNode node) {
    this.artist = node.path("artist").textValue();
    this.album = node.path("album").textValue();
    this.count = node.path("count").intValue();
  }

  @JsonProperty("artist") public String getArtist() {
    return artist;
  }

  @JsonProperty("artist") public void setArtist(String artist) {
    this.artist = artist;
  }

  @JsonProperty("album") public String getAlbum() {
    return album;
  }

  @JsonProperty("album") public void setAlbum(String album) {
    this.album = album;
  }

  @SuppressWarnings("unused") @JsonProperty("count") public int getCount() {
    return count;
  }

  @JsonProperty("count") public void setCount(int count) {
    this.count = count;
  }
}
