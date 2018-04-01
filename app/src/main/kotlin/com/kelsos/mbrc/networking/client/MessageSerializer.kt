package com.kelsos.mbrc.networking.client

interface MessageSerializer {
  fun serialize(message: SocketMessage): String
}