package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.interfaces.data.Mapper
import com.kelsos.mbrc.networking.discovery.DiscoveryMessage

object ConnectionMapper : Mapper<DiscoveryMessage, ConnectionSettingsEntity> {
  override fun map(from: DiscoveryMessage): ConnectionSettingsEntity {
    return ConnectionSettingsEntity().apply {
      address = from.address
      port = from.port
      name = from.name
    }
  }
}

fun DiscoveryMessage.toConnection(): ConnectionSettingsEntity {
  return ConnectionMapper.map(this)
}
