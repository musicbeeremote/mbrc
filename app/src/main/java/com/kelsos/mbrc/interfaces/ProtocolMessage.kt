package com.kelsos.mbrc.interfaces

interface ProtocolMessage {
  val type: String

  val data: Any
}