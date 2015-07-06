package com.kelsos.mbrc.data;

import com.fasterxml.jackson.databind.JsonNode;

public class TrackEntry {
  private String artist;
  private String title;
  private String src;

  public TrackEntry(JsonNode jNode) {
    this.artist = jNode.path("artist").textValue();
    this.title = jNode.path("title").textValue();
    this.src = jNode.path("src").textValue();
  }

  public String getArtist() {
    return artist;
  }

  public String getTitle() {
    return title;
  }

  public String getSrc() {
    return src;
  }
}
