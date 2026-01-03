package com.kelsos.mbrc.core.common.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Interface for observing connection state.
 */
interface ConnectionStateFlow {
  val connection: StateFlow<ConnectionStatus>
  val isConnected: Boolean
}

/**
 * Interface for publishing connection state updates.
 */
interface ConnectionStatePublisher : ConnectionStateFlow {
  fun updateConnection(connection: ConnectionStatus)
}

/**
 * Default implementation of connection state management.
 */
class ConnectionState : ConnectionStatePublisher {
  private val _connection = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Offline)

  override val connection: StateFlow<ConnectionStatus> = _connection.asStateFlow()

  override val isConnected: Boolean
    get() = _connection.value is ConnectionStatus.Connected

  override fun updateConnection(connection: ConnectionStatus) {
    _connection.value = connection
  }
}
