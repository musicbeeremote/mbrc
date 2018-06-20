package com.kelsos.mbrc.ui.minicontrol

import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerNext
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPlayPause
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPrevious
import javax.inject.Inject

class MiniControlPresenterImpl
@Inject
constructor(
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val userActionUseCase: UserActionUseCase
) : BasePresenter<MiniControlView>(), MiniControlPresenter {

  init {
    playerStatusLiveDataProvider.observe(this) {
      view().updateStatus(it)
    }

    playingTrackLiveDataProvider.observe(this) {
      view().updateTrackInfo(it)
    }
  }

  override fun next() {
    userActionUseCase.perform(UserAction.create(PlayerNext))
  }

  override fun previous() {
    userActionUseCase.perform(UserAction.create(PlayerPrevious))
  }

  override fun playPause() {
    userActionUseCase.perform(UserAction.create(PlayerPlayPause))
  }
}
