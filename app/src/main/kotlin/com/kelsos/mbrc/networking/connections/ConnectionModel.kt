package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.annotations.Connection.Status
import com.kelsos.mbrc.events.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.RequestConnectionStateEvent
import com.kelsos.mbrc.events.bus.RxBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionModel
@Inject
constructor(private val bus: RxBus) {
  var isConnectionActive: Boolean = false
    private set
  private var isHandShakeDone: Boolean = false

  init {
    isConnectionActive = false
    isHandShakeDone = false
    this.bus.register(this, RequestConnectionStateEvent::class.java, { notifyState() })
  }

  val connection: Int
    @Status
    get() {
      if (isConnectionActive && isHandShakeDone) {
        return Connection.ACTIVE
      } else if (isConnectionActive) {
        return Connection.ON
      }

      return Connection.OFF
    }

  fun setConnectionState(connectionActive: String) {
    this.isConnectionActive = java.lang.Boolean.parseBoolean(connectionActive)
    notifyState()
  }

  private fun notifyState() {
    bus.post(ConnectionStatusChangeEvent(connection))
  }

  fun setHandShakeDone(handShakeDone: Boolean) {
    this.isHandShakeDone = handShakeDone
    notifyState()
  }
}
