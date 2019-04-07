package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.networking.connections.Connection.ACTIVE
import com.kelsos.mbrc.networking.connections.Connection.ON
import com.kelsos.mbrc.networking.connections.ConnectionStatus

interface ConnectionStatusState : State<ConnectionStatus> {
  fun connected()

  fun active()

  fun disconnected()
}

class ConnectionStatusStateImpl : ConnectionStatusState,
  BaseState<ConnectionStatus>() {
  init {
    set(ConnectionStatus())
  }

  override fun connected() {
    set(ConnectionStatus(ON))
  }

  override fun active() {
    set(ConnectionStatus(ACTIVE))
  }

  override fun disconnected() {
    set(ConnectionStatus())
  }
}
