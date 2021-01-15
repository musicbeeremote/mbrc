package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.networking.discovery.DiscoveryMessage

class ConnectionMapper :
  Mapper<DiscoveryMessage, ConnectionSettingsEntity> {
  override fun map(from: DiscoveryMessage): ConnectionSettingsEntity {
    return ConnectionSettingsEntity().apply {
      address = from.address
      port = from.port
      name = from.name
    }
  }
}
