package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.content.active_status.MainDataModel
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.networking.SendProtocolMessage
import com.kelsos.mbrc.networking.SocketMessage
import javax.inject.Inject

class VolumeInteractorImpl
@Inject constructor(
    private val model: MainDataModel,
    private val bus: RxBus
) : VolumeInteractor {

  override fun increment() {
    val volume: Int
    val currentVolume = model.volume

    if (currentVolume <= 90) {
      val mod = currentVolume % DEFAULT_STEP

      when {
        mod == 0 -> volume = currentVolume + DEFAULT_STEP
        mod < 5 -> volume = currentVolume + (DEFAULT_STEP - mod)
        else -> volume = currentVolume + (20 - mod)
      }

    } else {
      volume = 100
    }

    send(volume)
  }

  override fun decrement() {
    val volume: Int
    val currentVolume = model.volume

    if (currentVolume >= 10) {
      val mod = currentVolume % DEFAULT_STEP

      when {
        mod == 0 -> volume = currentVolume - DEFAULT_STEP
        mod < 5 -> volume = currentVolume - (DEFAULT_STEP + mod)
        else -> volume = currentVolume - mod
      }
    } else {
      volume = 0
    }

    send(volume)
  }

  override fun reduceVolume() {
    if (model.isMute || model.volume == 0) {
      return
    }
    val volume = (model.volume * 0.2).toInt()

    send(volume)

  }

  /**
   * Sends a messages with the new volume value through the active socket
   *
   * @param volume The new volume value that will be send to the plugin.
   */
  private fun send(volume: Int) {
    bus.post(SendProtocolMessage(SocketMessage.create(Protocol.PlayerVolume, volume)))
  }

  companion object {
    const val DEFAULT_STEP = 10
  }
}
