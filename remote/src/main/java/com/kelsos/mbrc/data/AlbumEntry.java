package com.kelsos.mbrc.data;

import com.fasterxml.jackson.databind.JsonNode;

public class AlbumEntry {
  private String artist;
  private String album;
  private int count;

  public AlbumEntry(JsonNode node) {
    this.artist = node.path("artist").textValue();
    this.album = node.path("album").textValue();
    this.count = node.path("count").intValue();
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
