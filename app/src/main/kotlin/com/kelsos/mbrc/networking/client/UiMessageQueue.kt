package com.kelsos.mbrc.networking.client

interface UiMessageQueue {

  fun dispatch(code: Int, payload: Any? = null)

  companion object {
    const val NOT_ALLOWED = 1
    const val PARTY_MODE_COMMAND_UNAVAILABLE = 5
  }
}
