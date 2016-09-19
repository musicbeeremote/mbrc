package com.kelsos.mbrc.model

import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LyricsUpdatedEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsModel
@Inject
constructor(private val bus: RxBus) {
  private var lyrics: String

  init {
    lyrics = Const.EMPTY
  }

  fun setLyrics(lyrics: String?) {
    if (lyrics == null || this.lyrics == lyrics) {
      return
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
        .trim { it <= ' ' }

    bus.post(LyricsUpdatedEvent(this.lyrics))
  }

  fun getLyrics(): String {
    return this.lyrics
  }
}
