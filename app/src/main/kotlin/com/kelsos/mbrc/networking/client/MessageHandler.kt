package com.kelsos.mbrc.networking.client

interface MessageHandler {
  fun handleMessage(incoming: String)
}
