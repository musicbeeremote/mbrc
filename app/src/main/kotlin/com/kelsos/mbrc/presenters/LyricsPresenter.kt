package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.ui.views.LyricsView

interface LyricsPresenter {
  fun bind(view: LyricsView)
  fun onPause()
  fun onResume()
}
