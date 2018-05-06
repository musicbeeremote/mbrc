package com.kelsos.mbrc.networking

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketActivityChecker
@Inject
constructor(dispatchers: AppCoroutineDispatchers) {
  private var deferred: Deferred<Unit>? = null
  private var pingTimeoutListener: PingTimeoutListener? = null
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + dispatchers.network)

  fun start() {
    Timber.v("Starting activity checker")
    scope.launch { schedule() }
  }

  private suspend fun schedule() {
    cancel()
    deferred = scope.async {
      delay(DELAY.times(1000).toLong())
      Timber.v("Ping was more than %d seconds ago", DELAY)
      try {
        pingTimeoutListener?.onTimeout()
      } catch (e: Exception) {
        Timber.v(e, "calling the onTimeout method failed")
      }
    }
  }

  private suspend fun cancel() {
    deferred?.run {
      if (isActive) {
        cancelAndJoin()
      }
    }
  }

  fun stop() {
    Timber.v("Stopping activity checker")
    scope.launch { cancel() }
  }

  fun ping() {
    Timber.v("Received ping")
    scope.launch { schedule() }
  }

  fun setPingTimeoutListener(pingTimeoutListener: PingTimeoutListener?) {
    this.pingTimeoutListener = pingTimeoutListener
  }

  interface PingTimeoutListener {
    fun onTimeout()
  }

  companion object {
    private const val DELAY = 40
  }
}
