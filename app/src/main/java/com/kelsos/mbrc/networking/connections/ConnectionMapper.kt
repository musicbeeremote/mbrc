package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.networking.discovery.DiscoveryMessage

object ConnectionMapper : Mapper<DiscoveryMessage, ConnectionSettingsEntity> {
  override fun map(from: DiscoveryMessage): ConnectionSettingsEntity =
    ConnectionSettingsEntity().apply {
      address = from.address
      port = from.port
      name = from.name
    }
}

fun DiscoveryMessage.toConnection(): ConnectionSettingsEntity = ConnectionMapper.map(this)
