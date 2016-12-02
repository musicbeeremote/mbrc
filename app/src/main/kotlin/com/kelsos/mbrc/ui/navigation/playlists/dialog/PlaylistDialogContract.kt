package com.kelsos.mbrc.ui.navigation.playlists.dialog

import com.kelsos.mbrc.domain.Playlist

interface PlaylistDialogPresenter {
  fun load()

  fun bind(view: PlaylistDialogView)
}

interface PlaylistDialogView {
  fun update(playlists: List<Playlist>)
}
