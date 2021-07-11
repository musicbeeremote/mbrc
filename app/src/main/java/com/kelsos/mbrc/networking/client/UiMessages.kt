package com.kelsos.mbrc.networking.client

import kotlinx.coroutines.flow.MutableSharedFlow

interface UiMessages {
  val messages: MutableSharedFlow<UiMessage>
}
