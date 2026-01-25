package com.kelsos.mbrc.core.common.state

sealed class ConnectionStatus(val status: String) {
  data object Offline : ConnectionStatus("Offline")

  /**
   * Actively attempting to connect to the server.
   * @param cycle Current reconnection cycle (1-based), null for initial connection
   * @param maxCycles Maximum reconnection cycles before giving up
   */
  data class Connecting(val cycle: Int? = null, val maxCycles: Int = 3) :
    ConnectionStatus("Connecting")

  data object Authenticating : ConnectionStatus("Authenticating")

  data object Connected : ConnectionStatus("Connected")
}
