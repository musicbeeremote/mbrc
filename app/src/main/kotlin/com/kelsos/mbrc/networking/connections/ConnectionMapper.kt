package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.interfaces.data.Mapper
import com.kelsos.mbrc.networking.DiscoveryMessage

class ConnectionMapper : Mapper<DiscoveryMessage, ConnectionSettings> {
  override fun map(from: DiscoveryMessage): ConnectionSettings {
    return ConnectionSettings().apply {
      address = from.address
      port = from.port
      name = from.name
    }
  }
}
