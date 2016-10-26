package com.kelsos.mbrc.views

import com.kelsos.mbrc.mvp.BaseView

interface LyricsView : BaseView {
  fun updateLyrics(lyrics: List<String>)
}
