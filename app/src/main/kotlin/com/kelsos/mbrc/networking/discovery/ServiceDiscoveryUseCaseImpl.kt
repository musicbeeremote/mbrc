package com.kelsos.mbrc.networking.discovery

import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.discovery.DiscoveryStop.COMPLETE
import kotlinx.coroutines.runBlocking

class ServiceDiscoveryUseCaseImpl(
  private val serviceDiscovery: RemoteServiceDiscovery,
  private val connectionRepository: ConnectionRepository
) : ServiceDiscoveryUseCase {
  override fun discover(onDiscoveryTerminated: (status: Int) -> Unit) {
    serviceDiscovery.discover { status, setting ->
      if (status == COMPLETE) {
        runBlocking {
          connectionRepository.save(checkNotNull(setting) { " settings should be not null" })
        }
      }
      onDiscoveryTerminated(status)
    }
  }
}
