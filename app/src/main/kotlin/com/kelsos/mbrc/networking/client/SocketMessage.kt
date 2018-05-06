package com.kelsos.mbrc.networking.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.Protocol.Context
import com.kelsos.mbrc.networking.protocol.ProtocolPayload

class SocketMessage {
  @JsonProperty
  var context: String? = null

  @JsonProperty
  var data: Any? = null

  @SuppressWarnings("unused")
  constructor()

  private constructor(@Context context: String, data: Any) {
    this.context = context
    this.data = data
  }

  companion object {

    fun create(@Context context: String, data: Any = ""): SocketMessage {
      return SocketMessage(context, data)
    }
  }
}

fun SocketMessage.Companion.player(): SocketMessage {
  return create(Protocol.Player, "Android")
}

fun SocketMessage.Companion.protocol(payload: ProtocolPayload): SocketMessage {
  return create(Protocol.ProtocolTag, payload)
}