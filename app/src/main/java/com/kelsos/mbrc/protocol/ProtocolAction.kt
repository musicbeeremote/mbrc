package com.kelsos.mbrc.protocol

interface ProtocolAction {
  suspend fun execute(protocolMessage: ProtocolMessage)
}
