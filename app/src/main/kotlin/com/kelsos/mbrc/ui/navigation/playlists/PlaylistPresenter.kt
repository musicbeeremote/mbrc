package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.ui.navigation.playlists.PlaylistListView

interface PlaylistPresenter {
  fun bind(view: PlaylistListView)

  fun load()

  fun play(path: String)
}
