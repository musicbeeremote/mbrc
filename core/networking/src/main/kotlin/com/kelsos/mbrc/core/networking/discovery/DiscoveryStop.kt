package com.kelsos.mbrc.core.networking.discovery

import com.kelsos.mbrc.core.common.data.ConnectionSettings

sealed class DiscoveryStop {
  object NoWifi : DiscoveryStop()

  object NotFound : DiscoveryStop()

  class Complete(val settings: ConnectionSettings) : DiscoveryStop()
}
