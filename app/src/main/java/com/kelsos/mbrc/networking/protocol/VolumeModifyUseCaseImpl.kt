package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage

class VolumeModifyUseCaseImpl(
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val messageQueue: MessageQueue
) : VolumeModifyUseCase {

  override fun increment() {
    val volume: Int
    val currentVolume = playerStatusLiveDataProvider.getValue()?.volume ?: 0

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

  override fun decrement() {
    val volume: Int
    val currentVolume = playerStatusLiveDataProvider.getValue()?.volume ?: 0

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

  override fun reduceVolume() {
    playerStatusLiveDataProvider.getValue()?.run {
      if (mute || volume == 0) {
        return
      }
      send((volume * 0.2).toInt())
    }
  }

  /**
   * Sends a messages with the new volume value through the active socket
   *
   * @param volume The new volume value that will be send to the plugin.
   */
  private fun send(volume: Int) {
    messageQueue.queue(SocketMessage.create(Protocol.PlayerVolume, volume))
  }

  companion object {
    const val DEFAULT_STEP = 10
  }
}