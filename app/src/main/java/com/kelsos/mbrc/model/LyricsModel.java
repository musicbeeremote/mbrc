package com.kelsos.mbrc.model;

import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.LyricsUpdatedEvent;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LyricsModel {
  private String lyrics;
  private RxBus bus;

  @Inject
  public LyricsModel(RxBus bus) {
    this.bus = bus;
    lyrics = Const.EMPTY;
  }

  public void setLyrics(String lyrics) {
    if (lyrics == null || this.lyrics.equals(lyrics)) {
      return;
    }

    this.lyrics = lyrics.replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .trim();

    bus.post(new LyricsUpdatedEvent(this.lyrics));
  }

  public String getLyrics() {
    return this.lyrics;
  }
}
