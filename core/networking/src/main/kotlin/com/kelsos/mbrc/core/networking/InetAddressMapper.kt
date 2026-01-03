package com.kelsos.mbrc.core.networking

import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.common.data.Mapper
import java.net.InetSocketAddress
import java.net.SocketAddress

object InetAddressMapper : Mapper<ConnectionSettings, SocketAddress> {
  override fun map(from: ConnectionSettings): SocketAddress =
    InetSocketAddress(from.address, from.port)
}

fun ConnectionSettings.toSocketAddress(): SocketAddress = InetAddressMapper.map(this)
