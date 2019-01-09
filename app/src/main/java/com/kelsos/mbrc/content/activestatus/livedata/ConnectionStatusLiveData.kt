package com.kelsos.mbrc.content.activestatus.livedata

import com.kelsos.mbrc.networking.connections.Connection.ACTIVE
import com.kelsos.mbrc.networking.connections.Connection.ON
import com.kelsos.mbrc.networking.connections.ConnectionStatus

interface ConnectionStatusLiveDataProvider : LiveDataProvider<ConnectionStatus> {
  fun connected()

  fun active()

  fun disconnected()
}

class ConnectionStatusLiveDataProviderImpl : ConnectionStatusLiveDataProvider,
  BaseLiveDataProvider<ConnectionStatus>() {
  init {
    update(ConnectionStatus())
  }

  override fun connected() {
    update(ConnectionStatus(ON))
  }

  override fun active() {
    update(ConnectionStatus(ACTIVE))
  }

  override fun disconnected() {
    update(ConnectionStatus())
  }
}
