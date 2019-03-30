package com.kelsos.mbrc.networking.discovery

sealed class DiscoveryStop {
  object NoWifi : DiscoveryStop()
  object NotFound : DiscoveryStop()
  object Complete : DiscoveryStop()
}
