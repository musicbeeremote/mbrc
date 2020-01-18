package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.common.data.Mapper
import java.net.InetSocketAddress
import java.net.SocketAddress

class InetAddressMapper :
  Mapper<ConnectionSettingsEntity, SocketAddress> {
  override fun map(from: ConnectionSettingsEntity): SocketAddress {
    return InetSocketAddress(from.address, from.port)
  }
}