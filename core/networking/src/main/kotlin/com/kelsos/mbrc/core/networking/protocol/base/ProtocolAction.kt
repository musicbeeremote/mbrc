package com.kelsos.mbrc.core.networking.protocol.base

fun interface ProtocolAction {
  suspend fun execute(message: ProtocolMessage)
}
