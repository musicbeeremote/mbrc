package com.kelsos.mbrc.platform.mediasession

import androidx.media.VolumeProviderCompat
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteVolumeProvider
@Inject
constructor(
  private val statusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val userActionUseCase: UserActionUseCase
) : VolumeProviderCompat(VOLUME_CONTROL_ABSOLUTE, 100, 0) {

  init {
    val volume = statusLiveDataProvider.getValue()?.volume ?: 0
    super.setCurrentVolume(volume)
    // bus.register(this, VolumeChange::class.java) { super.setCurrentVolume(it.volume) }
  }

  override fun onSetVolumeTo(volume: Int) {
    post(UserAction.create(Protocol.PlayerVolume, volume))
    currentVolume = volume
  }

  override fun onAdjustVolume(direction: Int) {
    if (direction == 0) {
      return
    }
    val value = statusLiveDataProvider.getValue()?.volume ?: 0
    val volume = value.plus(direction)
      .coerceAtLeast(0)
      .coerceAtMost(100)
    post(UserAction.create(Protocol.PlayerVolume, volume))
    currentVolume = volume
  }

  private fun post(action: UserAction) {
    userActionUseCase.perform(action)
  }
}
