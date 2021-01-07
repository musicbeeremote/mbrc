package com.kelsos.mbrc.networking.client

interface MessageQueue : Runnable {
  fun start()

  fun stop()

  fun queue(message: SocketMessage)

  fun setOnMessageAvailable(onMessageAvailable: (message: SocketMessage) -> Unit)
}
