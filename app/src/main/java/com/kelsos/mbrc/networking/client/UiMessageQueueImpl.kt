package com.kelsos.mbrc.networking.client

import kotlinx.coroutines.flow.MutableSharedFlow

class UiMessageQueueImpl : UiMessages {
  override val messages = MutableSharedFlow<UiMessage>(extraBufferCapacity = EXTRA_BUFFER_CAPACITY)
  companion object {
    private const val EXTRA_BUFFER_CAPACITY = 5
  }
}

sealed class UiMessage {
  object NotAllowed : UiMessage()
  object PartyModeCommandUnavailable : UiMessage()
}
