package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.ConnectionSettings
import com.kelsos.mbrc.data.DiscoveryMessage

class ConnectionMapper : Mapper<DiscoveryMessage, ConnectionSettings> {
  override fun map(discoveryMessage: DiscoveryMessage): ConnectionSettings {
    val settings = ConnectionSettings()
    settings.address = discoveryMessage.address
    settings.port = discoveryMessage.port
    settings.name = discoveryMessage.name
    return settings
  }
}
