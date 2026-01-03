package com.kelsos.mbrc.core.common.state

sealed class ConnectionStatus(val status: String) {
  object Offline : ConnectionStatus("Offline")

  object Authenticating : ConnectionStatus("Authenticating")

  object Connected : ConnectionStatus("Connected")
}
