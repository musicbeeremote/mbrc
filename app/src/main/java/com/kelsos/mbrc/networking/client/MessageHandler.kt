package com.kelsos.mbrc.networking.client

interface MessageHandler {
  suspend fun process(message: SocketMessage)
}
