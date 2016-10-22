package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.interactors.PlaylistInteractor
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.ui.views.PlaylistDialogView
import timber.log.Timber

class PlaylistDialogPresenterImpl : PlaylistDialogPresenter {
  private var view: PlaylistDialogView? = null

  @Inject private lateinit var playlistInteractor: PlaylistInteractor

  override fun load() {
    playlistInteractor.getUserPlaylists()
        .task()
        .subscribe({ view?.update(it) }, { Timber.v(it, "Failed to load playlists") })
  }

  override fun bind(view: PlaylistDialogView) {

    this.view = view
  }
}
