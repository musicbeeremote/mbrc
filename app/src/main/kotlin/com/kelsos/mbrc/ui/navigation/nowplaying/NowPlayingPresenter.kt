package com.kelsos.mbrc.ui.navigation.nowplaying

import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.interactors.NowPlayingListInteractor
import com.kelsos.mbrc.interactors.nowplaying.NowPlayingActionInteractor
import timber.log.Timber
import javax.inject.Inject

class NowPlayingPresenter
@Inject constructor(private val nowPlayingListInteractor: NowPlayingListInteractor,
                    private val nowPlayingActionInteractor: NowPlayingActionInteractor) {
  private var view: NowPlayingView? = null

  fun bind(view: NowPlayingView) {
    this.view = view
  }

  fun loadData() {
    nowPlayingListInteractor.execute().subscribe({ view!!.updateAdapter(it) },
        { Timber.v(it) })
  }

  fun playTrack(track: QueueTrack) {
    nowPlayingActionInteractor.play(track.path).subscribe({
      if (it) {
        view!!.updatePlayingTrack(track)
      }
    }, { Timber.v(it) })
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
