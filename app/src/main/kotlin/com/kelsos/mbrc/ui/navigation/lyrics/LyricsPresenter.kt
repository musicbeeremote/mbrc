package com.kelsos.mbrc.ui.navigation.lyrics

import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.model.LyricsModel
import com.kelsos.mbrc.mvp.BasePresenter
import java.util.*
import javax.inject.Inject

class LyricsPresenter : BasePresenter<LyricsView>() {
  @Inject lateinit var model: LyricsModel

  fun load() {
    if (!isAttached) {
      return
    }

    updateLyrics(model.lyrics)
  }

  fun updateLyrics(text: String) {
    if (!isAttached) {
      return
    }
    val lyrics = ArrayList(Arrays.asList<String>(*text.split(Const.LYRICS_NEWLINE.toRegex())
        .dropLastWhile(String::isEmpty)
        .toTypedArray()))
    view?.updateLyrics(lyrics)
  }
}
