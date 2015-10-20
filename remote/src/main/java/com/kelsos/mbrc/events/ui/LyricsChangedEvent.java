package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.dto.track.Lyrics;

public class LyricsChangedEvent {

  private Lyrics lyrics;

  private LyricsChangedEvent(Lyrics lyrics) {
    this.lyrics = lyrics;
  }

  public Lyrics getLyrics() {
    return this.lyrics;
  }

  public static LyricsChangedEvent newInstance(Lyrics lyrics) {
    return new LyricsChangedEvent(lyrics);
  }
}
