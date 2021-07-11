package com.kelsos.mbrc.networking.client

import kotlinx.coroutines.flow.Flow

interface MessageQueue {
  suspend fun queue(message: SocketMessage)
  val messages: Flow<SocketMessage>
}
