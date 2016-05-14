package com.kelsos.mbrc.data;

import com.fasterxml.jackson.databind.JsonNode;

public class ArtistEntry {
  private String artist;
  private int count;

  public ArtistEntry(JsonNode node) {
    this.artist = node.path("artist").textValue();
    this.count = node.path("count").intValue();
  }

  public String getArtist() {
    return artist;
  }

  @SuppressWarnings("unused") public int getCount() {
    return count;
  }
}
