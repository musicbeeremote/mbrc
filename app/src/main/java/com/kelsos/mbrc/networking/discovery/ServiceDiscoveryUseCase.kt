package com.kelsos.mbrc.networking.discovery

interface ServiceDiscoveryUseCase {
  fun discover(onDiscoveryTerminated: (status: Int) -> Unit = {})
}