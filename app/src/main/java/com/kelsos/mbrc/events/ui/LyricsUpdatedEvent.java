package com.kelsos.mbrc.events.ui;

public class LyricsUpdatedEvent {

  private String lyrics;

  public LyricsUpdatedEvent(String lyrics) {
    this.lyrics = lyrics;
  }

  public String getLyrics() {
    return this.lyrics;
  }
}
