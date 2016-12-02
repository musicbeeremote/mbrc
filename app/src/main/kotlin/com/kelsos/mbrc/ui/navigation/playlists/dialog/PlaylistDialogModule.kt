package com.kelsos.mbrc.ui.navigation.playlists.dialog

import toothpick.config.Module

class PlaylistDialogModule : Module() {
  init {
    bind(PlaylistDialogPresenter::class.java)
        .to(PlaylistDialogPresenterImpl::class.java)
        .singletonInScope()
  }
}
