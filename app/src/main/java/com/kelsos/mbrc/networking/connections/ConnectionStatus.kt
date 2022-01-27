package com.kelsos.mbrc.networking.connections

sealed class ConnectionStatus {
  object Off : ConnectionStatus()

  object On : ConnectionStatus()

  object Active : ConnectionStatus()
}
