package com.kelsos.mbrc.rest.responses;

public class LyricsResponse {
  private String lyrics;

  public LyricsResponse(String lyrics) {
    this.lyrics = lyrics;
  }

  public LyricsResponse() { }

  public String getLyrics() {
    return lyrics;
  }
}
