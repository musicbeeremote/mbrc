package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.data.DiscoveryMessage

class ConnectionMapper : Mapper<DiscoveryMessage, ConnectionSettings> {
  override fun map(from: DiscoveryMessage): ConnectionSettings {
    return ConnectionSettings().apply {
      address = from.address
      port = from.port
      name = from.name
    }
  }
}
