package com.kelsos.mbrc.ui.navigation.playlists.dialog

import com.kelsos.mbrc.ui.navigation.playlists.dialog.PlaylistDialogView

interface PlaylistDialogPresenter {
  fun load()

  fun bind(view: PlaylistDialogView)
}
