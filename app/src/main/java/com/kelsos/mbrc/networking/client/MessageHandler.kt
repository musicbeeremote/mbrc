package com.kelsos.mbrc.networking.client

interface MessageHandler {
  suspend fun handleMessage(incoming: String)

  fun start()

  fun stop()
}
