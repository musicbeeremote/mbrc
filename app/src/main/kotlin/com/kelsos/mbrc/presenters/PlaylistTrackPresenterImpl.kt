package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.PlaylistTrack
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.interactors.playlists.PlaylistTrackInteractor
import com.kelsos.mbrc.ui.views.PlaylistTrackView
import timber.log.Timber

class PlaylistTrackPresenterImpl : PlaylistTrackPresenter {
  private var view: PlaylistTrackView? = null

  @Inject private lateinit var playlistTrackInteractor: PlaylistTrackInteractor
  @Inject private lateinit var queueInteractor: QueueInteractor

  override fun bind(view: PlaylistTrackView) {
    this.view = view
  }

  override fun load(longExtra: Long) {
    playlistTrackInteractor.execute(longExtra)
        .task()
        .subscribe({ view?.update(it) },
            {
              view?.showErrorWhileLoading()
              Timber.e(it, "")
            })
  }

  override fun queue(track: PlaylistTrack, @Queue.Action action: String) {
    if (track.path.isNullOrEmpty()) {
      return
    }

    queueInteractor.execute(action, track.path).subscribe({

    }) { Timber.e(it, "Queueing failed") }
  }
}
