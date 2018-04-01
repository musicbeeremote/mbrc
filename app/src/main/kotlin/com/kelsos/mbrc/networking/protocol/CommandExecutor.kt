package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.events.MessageEvent

interface CommandExecutor : Runnable {
  fun start()

  fun stop()

  fun processEvent(event: MessageEvent)
}
