package com.kelsos.mbrc.ui.navigation.library.albumtracks

import toothpick.config.Module

class AlbumTracksModule : Module() {
  init {
    bind(AlbumTracksPresenter::class.java)
      .to(AlbumTracksPresenterImpl::class.java)
      .singletonInScope()
  }
}
