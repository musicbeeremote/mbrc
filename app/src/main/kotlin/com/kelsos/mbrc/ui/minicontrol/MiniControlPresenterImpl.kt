package com.kelsos.mbrc.ui.minicontrol

import androidx.lifecycle.Observer
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerNext
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPlayPause
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPrevious
import javax.inject.Inject

@MiniControlFragment.Presenter
class MiniControlPresenterImpl
@Inject
constructor(
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : BasePresenter<MiniControlView>(), MiniControlPresenter {

  init {
    playerStatusLiveDataProvider.get().observe(this, Observer {
      if (it == null) {
        return@Observer
      }
      view().updateState(it.playState)
    })

    playingTrackLiveDataProvider.get().observe(this, Observer {
      if (it == null) {
        return@Observer
      }

      view().updateTrackInfo(it)
    })
  }

  override fun next() {
    post(PlayerNext)
  }

  override fun previous() {
    post(PlayerPrevious)
  }

  override fun playPause() {
    post(PlayerPlayPause)
  }

  fun post(action: String) {

  }

}