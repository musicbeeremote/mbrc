package com.kelsos.mbrc.protocol

interface ProtocolMessage {
  val type: String

  val data: Any
}