package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage
import kotlinx.coroutines.flow.firstOrNull

class VolumeModifyUseCaseImpl(
  private val appState: AppState,
  private val queue: MessageQueue
) : VolumeModifyUseCase {

  private suspend fun currentVolume() = appState.playerStatus.firstOrNull()?.volume ?: 0
  private suspend fun isMute() = appState.playerStatus.firstOrNull()?.mute ?: false

  override suspend fun increment() {
    val volume: Int
    val currentVolume = currentVolume()

    volume = if (currentVolume <= MAX_VOLUME - DEFAULT_STEP) {
      val mod = currentVolume % DEFAULT_STEP
      when {
        mod == MIN_VOLUME -> currentVolume + DEFAULT_STEP
        mod < HALF_STEP -> currentVolume + (DEFAULT_STEP - mod)
        else -> currentVolume + (DOUBLE_STEP - mod)
      }
    } else {
      MAX_VOLUME
    }

    send(volume)
  }

  override suspend fun decrement() {
    val volume: Int
    val currentVolume = currentVolume()

    volume = if (currentVolume >= DEFAULT_STEP) {
      val mod = currentVolume % DEFAULT_STEP

      when {
        mod == MIN_VOLUME -> currentVolume - DEFAULT_STEP
        mod < HALF_STEP -> currentVolume - (DEFAULT_STEP + mod)
        else -> currentVolume - mod
      }
    } else {
      MIN_VOLUME
    }

    send(volume)
  }

  override suspend fun reduceVolume() {
    val volume = currentVolume()
    val mute = isMute()
    if (mute || volume == 0) {
      return
    }
    send((volume * VOLUME_PERCENTAGE).toInt())
  }

  /**
   * Sends a messages with the new volume value through the active socket
   *
   * @param volume The new volume value that will be send to the plugin.
   */
  private suspend fun send(volume: Int) {
    queue.queue(SocketMessage.create(Protocol.PlayerVolume, volume))
  }

  companion object {
    const val DEFAULT_STEP = 10
    const val HALF_STEP = DEFAULT_STEP.div(other = 2)
    const val DOUBLE_STEP = DEFAULT_STEP.times(other = 2)
    const val MAX_VOLUME = 100
    const val MIN_VOLUME = 0
    const val VOLUME_PERCENTAGE = 0.2
  }
}
