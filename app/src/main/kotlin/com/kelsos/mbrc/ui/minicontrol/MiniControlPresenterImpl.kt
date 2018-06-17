package com.kelsos.mbrc.ui.minicontrol

import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerNext
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPlayPause
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPrevious
import javax.inject.Inject

class MiniControlPresenterImpl
@Inject
constructor(
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : BasePresenter<MiniControlView>(), MiniControlPresenter {

  init {
    playerStatusLiveDataProvider.observe(this) {
      view().updateState(it.state)
    }

    playingTrackLiveDataProvider.observe(this) {
      view().updateTrackInfo(it)
    }
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
