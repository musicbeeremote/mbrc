package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.PlaylistActionInteractor
import com.kelsos.mbrc.interactors.PlaylistInteractor
import timber.log.Timber
import javax.inject.Inject

class PlaylistPresenterImpl
@Inject constructor(private val actionInteractor: PlaylistActionInteractor,
                    private val playlistInteractor: PlaylistInteractor) : PlaylistPresenter {
  private var view: PlaylistListView? = null

  override fun bind(view: PlaylistListView) {
    this.view = view
  }

  override fun load() {
    playlistInteractor.getAllPlaylists().task().subscribe({
      view?.update(it)
    }, { Timber.v(it) })
  }

  override fun play(path: String) {
    actionInteractor.play(path).subscribe({

    }, {

    })
  }
}
