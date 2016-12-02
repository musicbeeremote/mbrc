package com.kelsos.mbrc.ui.navigation.playlists.tracks

import toothpick.config.Module

class PlaylistTrackModule : Module() {
  init {
    bind(PlaylistTrackPresenter::class.java)
        .to(PlaylistTrackPresenterImpl::class.java)
        .singletonInScope()
  }
}
