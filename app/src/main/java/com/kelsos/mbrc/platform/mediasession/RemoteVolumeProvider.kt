package com.kelsos.mbrc.platform.mediasession

import androidx.media.VolumeProviderCompat
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol

class RemoteVolumeProvider(
  private val statusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val userActionUseCase: UserActionUseCase
) : VolumeProviderCompat(VOLUME_CONTROL_ABSOLUTE, 100, 0) {

  init {
    val volume = statusLiveDataProvider.getValue()?.volume ?: 0
    super.setCurrentVolume(volume)
    //bus.register(this, VolumeChange::class.java, { super.setCurrentVolume(it.volume) })
  }

  override fun onSetVolumeTo(volume: Int) {
    post(UserAction.create(Protocol.PlayerVolume, volume))
  }

  override fun onAdjustVolume(direction: Int) {
    val previousVolume = statusLiveDataProvider.getValue()?.volume ?: 0

    if (direction > 0) {
      val volume = previousVolume + 5
      post(UserAction.create(Protocol.PlayerVolume, if (volume < 100) volume else 100))
    } else {
      val volume = previousVolume - 5
      post(UserAction.create(Protocol.PlayerVolume, if (volume > 0) volume else 0))
    }
  }

  private fun post(action: UserAction) {
    userActionUseCase.perform(action)
  }
}