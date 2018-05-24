package com.kelsos.mbrc.networking

import com.kelsos.mbrc.networking.client.IClientConnectionManager
import javax.inject.Inject

class ClientConnectionUseCaseImpl
@Inject
constructor(
  private val connectionManager: IClientConnectionManager
) :
  ClientConnectionUseCase {
  override fun connect() {
    connectionManager.stop()
    connectionManager.start()
  }
}
