package com.kelsos.mbrc.networking.client

interface MessageDeserializer {
  fun deserialize(message: String): SocketMessage
}