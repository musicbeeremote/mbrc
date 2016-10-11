package com.kelsos.mbrc.ui.activities.profile.album

import toothpick.config.Module


class AlbumTracksModule : Module() {
  init {
    bind(AlbumTracksPresenter::class.java).to(AlbumTracksPresenterImpl::class.java).singletonInScope()
  }
}
