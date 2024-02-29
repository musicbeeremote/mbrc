package com.kelsos.mbrc.protocol

import com.kelsos.mbrc.networking.protocol.Protocol

fun createTestProtocolMessage(status: Boolean, empty: Boolean = false) = object : ProtocolMessage {
  override val type: Protocol
    get() = Protocol.PlayerScrobble
  override val data: Any
    get() = if (!empty) status else ""
}
