package com.kelsos.mbrc.ui.navigation.playlists.dialog

import javax.inject.Inject

class PlaylistDialogPresenterImpl
@Inject constructor() : PlaylistDialogPresenter {
  private var view: PlaylistDialogView? = null

  override fun load() {
    TODO()
  }

  override fun bind(view: PlaylistDialogView) {

    this.view = view
  }
}
