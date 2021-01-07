package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.common.state.State
import com.kelsos.mbrc.networking.connections.ConnectionStatus

interface ConnectionStatusState : State<ConnectionStatus> {
  fun connected()

  fun active()

  fun disconnected()
}

class ConnectionStatusStateImpl : ConnectionStatusState,
  BaseState<ConnectionStatus>() {
  init {
    set(ConnectionStatus.Off)
  }

  override fun connected() {
    set(ConnectionStatus.On)
  }

  override fun active() {
    set(ConnectionStatus.Active)
  }

  override fun disconnected() {
    set(ConnectionStatus.Off)
  }
}
