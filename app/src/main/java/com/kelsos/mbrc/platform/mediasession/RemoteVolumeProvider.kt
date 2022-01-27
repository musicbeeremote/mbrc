package com.kelsos.mbrc.platform.mediasession

import androidx.media.VolumeProviderCompat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.performUserAction
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class RemoteVolumeProvider(
  appState: AppState,
  dispatchers: AppCoroutineDispatchers,
  private val userActionUseCase: UserActionUseCase,
) : VolumeProviderCompat(VOLUME_CONTROL_ABSOLUTE, MAX_VOLUME, MIN_VOLUME) {
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.network)
  private val playerStatus: Flow<PlayerStatusModel> = appState.playerStatus

  private suspend fun currentVolume() = playerStatus.firstOrNull()?.volume ?: MIN_VOLUME

  init {
    scope.launch {
      val volume = currentVolume()
      super.setCurrentVolume(volume)
      playerStatus.collect { status ->
        if (super.getCurrentVolume() != status.volume) {
          super.setCurrentVolume(status.volume)
        }
      }
    }
  }

  override fun onSetVolumeTo(volume: Int) {
    scope.launch {
      userActionUseCase.performUserAction(Protocol.PlayerVolume, volume)
    }
    currentVolume = volume
  }

  override fun onAdjustVolume(direction: Int) {
    if (direction == 0) {
      return
    }

    scope.launch {
      val previousVolume = currentVolume()
      val newVolume =
        previousVolume
          .plus(direction)
          .coerceAtLeast(MIN_VOLUME)
          .coerceAtMost(MAX_VOLUME)
      userActionUseCase.performUserAction(Protocol.PlayerVolume, newVolume)
      currentVolume = newVolume
    }
  }

  companion object {
    const val MIN_VOLUME = 0
    const val MAX_VOLUME = 100
  }
}
