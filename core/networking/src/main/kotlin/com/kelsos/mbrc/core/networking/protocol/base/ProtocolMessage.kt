package com.kelsos.mbrc.core.networking.protocol.base

interface ProtocolMessage {
  val type: Protocol

  val data: Any
}

fun ProtocolMessage.asBoolean(): Boolean = data as? Boolean == true
