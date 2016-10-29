package com.kelsos.mbrc.ui.navigation.lyrics

import com.kelsos.mbrc.mvp.BaseView

interface LyricsView : BaseView {
  fun updateLyrics(lyrics: List<String>)
}
