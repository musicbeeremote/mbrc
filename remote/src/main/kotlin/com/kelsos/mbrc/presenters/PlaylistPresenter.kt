package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.ui.views.PlaylistListView

interface PlaylistPresenter {
  fun bind(view: PlaylistListView)

  fun load()

  fun play(path: String)
}
