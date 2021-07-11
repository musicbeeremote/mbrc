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

    volume = if (currentVolume <= 90) {
      val mod = currentVolume % DEFAULT_STEP
      when {
        mod == 0 -> currentVolume + DEFAULT_STEP
        mod < 5 -> currentVolume + (DEFAULT_STEP - mod)
        else -> currentVolume + (20 - mod)
      }
    } else {
      100
    }

    send(volume)
  }

  override suspend fun decrement() {
    val volume: Int
    val currentVolume = currentVolume()

    volume = if (currentVolume >= 10) {
      val mod = currentVolume % DEFAULT_STEP

      when {
        mod == 0 -> currentVolume - DEFAULT_STEP
        mod < 5 -> currentVolume - (DEFAULT_STEP + mod)
        else -> currentVolume - mod
      }
    } else {
      0
    }

    send(volume)
  }

  override suspend fun reduceVolume() {
    val volume = currentVolume()
    val mute = isMute()
    if (mute || volume == 0) {
      return
    }
    send((volume * 0.2).toInt())
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
  }
}
