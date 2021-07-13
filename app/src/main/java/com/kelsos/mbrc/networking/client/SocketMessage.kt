package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocketMessage(
  @Json(name = "context")
  val context: String,
  @Json(name = "data")
  val data: Any = ""
) {

  override fun toString(): String {
    return "{ context=$context, data=$data }"
  }

  companion object {
    fun create(protocol: Protocol, data: Any = ""): SocketMessage {
      return SocketMessage(protocol.context, data)
    }
  }
}

fun SocketMessage.Companion.player(): SocketMessage {
  return create(Protocol.Player, "Android")
}

fun SocketMessage.Companion.protocol(payload: ProtocolPayload): SocketMessage {
  return create(Protocol.ProtocolTag, payload)
}
