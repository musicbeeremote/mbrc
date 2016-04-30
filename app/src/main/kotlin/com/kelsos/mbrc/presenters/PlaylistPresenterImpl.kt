package com.kelsos.mbrc.presenters

import com.google.inject.Inject
import com.kelsos.mbrc.interactors.PlaylistActionInteractor
import com.kelsos.mbrc.interactors.PlaylistInteractor
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.ui.views.PlaylistListView
import roboguice.util.Ln

class PlaylistPresenterImpl : PlaylistPresenter {
  private var view: PlaylistListView? = null

  @Inject private lateinit var playlistInteractor: PlaylistInteractor
  @Inject private lateinit var actionInteractor: PlaylistActionInteractor

  override fun bind(view: PlaylistListView) {
    this.view = view
  }

  override fun load() {
    playlistInteractor.allPlaylists.task().subscribe({
      view?.update(it)
    }, { Ln.v(it) })
  }

  override fun play(path: String) {
    actionInteractor.play(path).subscribe({

    }, {

    })
  }
}
