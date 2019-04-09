package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.Protocol.Context
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocketMessage(
  @Context
  @Json(name = "context")
  val context: String,
  @Json(name = "data")
  val data: Any = ""
) {

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