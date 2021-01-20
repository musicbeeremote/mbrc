package com.kelsos.mbrc.networking.client

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class UiMessageQueueImpl : UiMessageQueue {
  override val messages: MutableSharedFlow<UiMessage> = MutableSharedFlow(
    0,
    10,
    BufferOverflow.SUSPEND
  )
  override fun emit(message: UiMessage) {
    messages.tryEmit(message)
  }
}

sealed class UiMessage(val payload: Any? = null) {
  object NotAllowed : UiMessage()
  object PartyModeCommandNotAvailable : UiMessage()
}
