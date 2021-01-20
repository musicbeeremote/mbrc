package com.kelsos.mbrc.networking.client

import kotlinx.coroutines.flow.SharedFlow

interface UiMessageQueue {
  val messages: SharedFlow<UiMessage>
  fun emit(message: UiMessage)
}
