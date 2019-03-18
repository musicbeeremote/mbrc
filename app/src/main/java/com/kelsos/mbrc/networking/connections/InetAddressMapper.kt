package com.kelsos.mbrc.networking.connections

import com.kelsos.mbrc.interfaces.data.Mapper
import java.net.InetSocketAddress
import java.net.SocketAddress

object InetAddressMapper : Mapper<ConnectionSettings, SocketAddress> {
  override fun map(from: ConnectionSettings): SocketAddress {
    return InetSocketAddress(from.address, from.port)
  }
}

fun ConnectionSettings.toSocketAddress(): SocketAddress {
  return InetAddressMapper.map(this)
}
