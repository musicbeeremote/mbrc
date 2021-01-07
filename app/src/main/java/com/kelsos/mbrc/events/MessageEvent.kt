package com.kelsos.mbrc.events

import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.protocol.ProtocolMessage

data class MessageEvent(
  override var type: Protocol,
  override var data: Any = ""
) : ProtocolMessage
