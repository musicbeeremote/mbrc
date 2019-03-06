package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.interfaces.data.Mapper
import java.net.InetSocketAddress
import java.net.SocketAddress

object InetAddressMapper : Mapper<ConnectionSettingsEntity, SocketAddress> {
  override fun map(from: ConnectionSettingsEntity): SocketAddress {
    return InetSocketAddress(from.address, from.port)
  }
}

fun ConnectionSettingsEntity.toSocketAddress(): SocketAddress {
  return InetAddressMapper.map(this)
}
