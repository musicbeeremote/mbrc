package com.kelsos.mbrc.networking

import com.kelsos.mbrc.common.utilities.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import timber.log.Timber

class SocketActivityChecker(
  dispatchers: AppDispatchers
) {
  private var supervisor = SupervisorJob()
  private var scope = CoroutineScope(supervisor + dispatchers.io)
  private var pingTimeoutListener: PingTimeoutListener? = null
  private var timer: Deferred<Unit>? = null

  fun start() {
    Timber.v("Setting next timeout check")
    timer = scope.async {
      delay(DELAY)
      Timber.v("Ping was more than %d seconds ago", DELAY)
      pingTimeoutListener?.onTimeout()
    }
  }

  fun stop() {
    Timber.v("Stopping activity checker")
    dispose()
  }

  fun ping() {
    Timber.v("Received ping")
    dispose()
    start()
  }

  private fun dispose() {
    if (timer?.isActive == true) {
      timer?.cancel()
    }
  }

  fun setPingTimeoutListener(pingTimeoutListener: PingTimeoutListener?) {
    this.pingTimeoutListener = pingTimeoutListener
  }

  interface PingTimeoutListener {
    fun onTimeout()
  }

  companion object {
    private const val DELAY = 40 * 1000L
  }
}
