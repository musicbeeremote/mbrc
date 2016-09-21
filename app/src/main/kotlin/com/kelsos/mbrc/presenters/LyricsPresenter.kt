package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.model.LyricsModel
import com.kelsos.mbrc.views.LyricsView
import java.util.*
import javax.inject.Inject

class LyricsPresenter : BasePresenter<LyricsView>() {
  @Inject lateinit var model: LyricsModel

  fun load() {
    if (!isAttached) {
      return
    }

    updateLyrics(model.getLyrics())
  }

  fun updateLyrics(text: String) {
    if (!isAttached) {
      return
    }
    val lyrics = ArrayList(Arrays.asList<String>(*text.split(Const.LYRICS_NEWLINE.toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()))
    view?.updateLyrics(lyrics)
  }
}
