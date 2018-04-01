package com.kelsos.mbrc.networking.client

import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.networking.protocol.Protocol.Context

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
