package com.kelsos.mbrc.protocol

import com.kelsos.mbrc.networking.protocol.Protocol

interface ProtocolMessage {
  val type: Protocol

  val data: Any
}
