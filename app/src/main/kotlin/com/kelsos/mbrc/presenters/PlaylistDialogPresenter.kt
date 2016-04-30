package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.ui.views.PlaylistDialogView

interface PlaylistDialogPresenter {
  fun load()

  fun bind(view: PlaylistDialogView)
}
