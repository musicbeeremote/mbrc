package com.kelsos.mbrc.networking.client

import timber.log.Timber
import javax.inject.Inject

class UiMessageQueueImpl
@Inject
constructor(): UiMessageQueue {
  override fun dispatch(code: Int, payload: Any?) {
    Timber.v("ui message $code received")
  }
}