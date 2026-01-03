package com.kelsos.mbrc.core.networking

interface ClientConnectionUseCase {
  fun connect(reset: Boolean = false)
  fun disconnect()
}

class ClientConnectionUseCaseImpl(private val connectionManager: ClientConnectionManager) :
  ClientConnectionUseCase {
  override fun connect(reset: Boolean) {
    if (reset) {
      connectionManager.stop()
    }
    connectionManager.start()
  }

  override fun disconnect() {
    connectionManager.stop()
  }
}
