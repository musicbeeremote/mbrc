package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.events.MessageEvent

interface CommandExecutor {
  fun start()

  fun stop()

  suspend fun queue(event: MessageEvent)
}
