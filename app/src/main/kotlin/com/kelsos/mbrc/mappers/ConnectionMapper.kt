package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.DiscoveryMessage
import com.kelsos.mbrc.data.dao.ConnectionSettings

class ConnectionMapper : Mapper<DiscoveryMessage, ConnectionSettings> {
  override fun map(from: DiscoveryMessage): ConnectionSettings {
    val settings = ConnectionSettings()
    settings.address = from.address
    settings.port = from.port
    settings.name = from.name
    return settings
  }
}
