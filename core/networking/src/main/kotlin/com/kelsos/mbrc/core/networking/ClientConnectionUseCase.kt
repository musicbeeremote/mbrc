package com.kelsos.mbrc.core.networking

/**
 * Information about the current reconnection cycle.
 * Used to display progress in the UI during connection attempts.
 */
data class ConnectionCycleInfo(val cycle: Int, val maxCycles: Int)

interface ClientConnectionUseCase {
  fun connect(reset: Boolean = false, cycleInfo: ConnectionCycleInfo? = null)
  fun disconnect()
}

class ClientConnectionUseCaseImpl(private val connectionManager: ClientConnectionManager) :
  ClientConnectionUseCase {
  override fun connect(reset: Boolean, cycleInfo: ConnectionCycleInfo?) {
    if (reset) {
      connectionManager.stop()
    }
    connectionManager.start(cycleInfo)
  }

  override fun disconnect() {
    connectionManager.stop()
  }
}
