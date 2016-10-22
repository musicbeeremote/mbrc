package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.interactors.NowPlayingListInteractor
import com.kelsos.mbrc.interactors.nowplaying.NowPlayingActionInteractor
import com.kelsos.mbrc.ui.views.NowPlayingView
import roboguice.util.Ln

class NowPlayingPresenter {
  private var view: NowPlayingView? = null
  @Inject private lateinit var nowPlayingListInteractor: NowPlayingListInteractor
  @Inject private lateinit var nowPlayingActionInteractor: NowPlayingActionInteractor

  fun bind(view: NowPlayingView) {
    this.view = view
  }

  fun loadData() {
    nowPlayingListInteractor.execute().subscribe({ view!!.updateAdapter(it) },
        { Ln.v(it) })
  }

  fun playTrack(track: QueueTrack) {
    nowPlayingActionInteractor.play(track.path).subscribe({
      if (it) {
        view!!.updatePlayingTrack(track)
      }
    }, { Ln.v(it) })
  }

  fun removeItem(position: Int) {
    nowPlayingActionInteractor.remove(position.toLong()).subscribe {

    }
  }

  fun moveItem(from: Int, to: Int) {
    nowPlayingActionInteractor.move(from, to).subscribe {

    }
  }
}
