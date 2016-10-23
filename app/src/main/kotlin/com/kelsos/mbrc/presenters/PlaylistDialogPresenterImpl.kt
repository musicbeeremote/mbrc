package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.PlaylistInteractor
import com.kelsos.mbrc.ui.views.PlaylistDialogView
import timber.log.Timber
import javax.inject.Inject

class PlaylistDialogPresenterImpl
@Inject constructor(private var playlistInteractor: PlaylistInteractor) : PlaylistDialogPresenter {
  private var view: PlaylistDialogView? = null

  override fun load() {
    playlistInteractor.getUserPlaylists()
        .task()
        .subscribe({ view?.update(it) }, { Timber.v(it, "Failed to load playlists") })
  }

  override fun bind(view: PlaylistDialogView) {

    this.view = view
  }
}
