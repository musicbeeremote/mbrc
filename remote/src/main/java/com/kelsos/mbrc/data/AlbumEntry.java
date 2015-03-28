package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;

public class AlbumEntry {
  private String artist;
  private String album;
  private int count;

  public AlbumEntry(JsonNode node) {
    this.artist = node.path("artist").getTextValue();
    this.album = node.path("album").getTextValue();
    this.count = node.path("count").getIntValue();
  }

  public String getArtist() {
    return artist;
  }

  public String getAlbum() {
    return album;
  }

  @SuppressWarnings("unused") public int getCount() {
    return count;
  }
}
