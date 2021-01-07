package com.kelsos.mbrc.networking

import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SocketActivityChecker {
  private var disposable: Disposable? = null
  private var pingTimeoutListener: PingTimeoutListener? = null

  fun start() {
    Timber.v("Starting activity checker")
    disposable = subscribe
  }

  private val subscribe: Disposable
    get() = Completable.timer(DELAY.toLong(), TimeUnit.SECONDS).subscribe({
      Timber.v("Ping was more than %d seconds ago", DELAY)
      pingTimeoutListener?.onTimeout()
    }) { Timber.v("Subscription failed") }

  fun stop() {
    Timber.v("Stopping activity checker")
    dispose()
  }

  fun ping() {
    Timber.v("Received ping")
    dispose()
    disposable = subscribe
  }

  private fun dispose() {
    disposable?.let {
      if (!it.isDisposed) {
        it.dispose()
      }
    }
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
