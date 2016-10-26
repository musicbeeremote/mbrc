package com.kelsos.mbrc.ui.navigation.lyrics

import com.kelsos.mbrc.ui.navigation.lyrics.LyricsView

interface LyricsPresenter {
  fun bind(view: LyricsView)
  fun onPause()
  fun onResume()
}
