package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import javax.inject.Inject

class VolumeDialogPresenterImpl
@Inject
constructor(
  private val userActionUseCase: UserActionUseCase,
  playerStatusLiveDataProvider: PlayerStatusLiveDataProvider
) : VolumeDialogPresenter, BasePresenter<VolumeView>() {

  private val volumeRelay: MutableSharedFlow<Int> = MutableStateFlow(0)

  init {
    playerStatusLiveDataProvider.observe(this) {
      view().update(it)
    }
  }

  override fun attach(view: VolumeView) {
    super.attach(view)
    volumeRelay.sample(800).onEach { volume ->
      userActionUseCase.perform(UserAction.create(Protocol.PlayerVolume, volume))
    }.launchIn(scope)
  }

  override fun mute() {
    userActionUseCase.perform(UserAction.toggle(Protocol.PlayerMute))
  }

  override fun changeVolume(volume: Int) {
    volumeRelay.tryEmit(volume)
  }
}
