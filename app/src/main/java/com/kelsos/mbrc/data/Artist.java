package com.kelsos.mbrc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Artist {

  @JsonProperty("artist") private String artist;

  @JsonProperty("count") private int count;

  public Artist() {

  }

  public Artist(JsonNode node) {
    this.artist = node.path("artist").textValue();
    this.count = node.path("count").intValue();
  }

  @JsonProperty("artist") public String getArtist() {
    return artist;
  }

  @JsonProperty("artist") public void setArtist(String artist) {
    this.artist = artist;
  }

  @SuppressWarnings("unused")
  @JsonProperty("count") public int getCount() {
    return count;
  }

  @JsonProperty("count") public void setCount(int count) {
    this.count = count;
  }
}
