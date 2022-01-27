package com.kelsos.mbrc.networking.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface MessageHandler {
  fun listen(
    scope: CoroutineScope,
    messages: Flow<SocketMessage>,
  )

  fun handleOutgoing(
    scope: CoroutineScope,
    send: (SocketMessage) -> Result<Unit>,
  )

  suspend fun startHandshake()
}
