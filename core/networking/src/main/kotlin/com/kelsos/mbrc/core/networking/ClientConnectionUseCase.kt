package com.kelsos.mbrc.core.networking

import com.kelsos.mbrc.core.networking.client.PendingCommandBuffer

/**
 * Information about the current reconnection cycle.
 * Used to display progress in the UI during connection attempts.
 */
data class ConnectionCycleInfo(val cycle: Int, val maxCycles: Int)

interface ClientConnectionUseCase {
  fun connect(reset: Boolean = false, cycleInfo: ConnectionCycleInfo? = null)
  fun disconnect()
}

class ClientConnectionUseCaseImpl(
  private val connectionManager: ClientConnectionManager,
  private val pendingCommands: PendingCommandBuffer
) : ClientConnectionUseCase {
  override fun connect(reset: Boolean, cycleInfo: ConnectionCycleInfo?) {
    if (reset) {
      connectionManager.stop()
    }
    connectionManager.start(cycleInfo)
  }

  override fun disconnect() {
    // Deliberately leaving: buffered commands belong to the session the user just ended, and must
    // not fire against the next one. A reconnect goes through connect(), which keeps them.
    pendingCommands.clear()
    connectionManager.stop()
  }
}
