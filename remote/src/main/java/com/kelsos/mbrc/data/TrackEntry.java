package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;

public class TrackEntry {
  private String artist;
  private String title;
  private String src;

  public TrackEntry(JsonNode jNode) {
    this.artist = jNode.path("artist").getTextValue();
    this.title = jNode.path("title").getTextValue();
    this.src = jNode.path("src").getTextValue();
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
