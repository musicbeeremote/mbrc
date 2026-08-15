package com.kelsos.mbrc.core.common.state

import androidx.compose.runtime.Stable

@Stable
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

  /**
   * The local network cannot be reached because the user has not granted permission for it.
   *
   * Terminal rather than transient: retrying cannot succeed, so nothing should present this as a
   * connection that is still being attempted. The only way out is the user granting access.
   */
  data object LocalNetworkDenied : ConnectionStatus("LocalNetworkDenied")
}
