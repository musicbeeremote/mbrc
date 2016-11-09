package com.kelsos.mbrc.helper

import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class ProgressSeekerHelper
@Inject constructor(@Named("main") val scheduler: Scheduler) {
  private var progressUpdate: ProgressUpdate? = null
  private var subscription: Subscription? = null

  fun start(duration: Int) {
    subscription = Observable.interval(1, TimeUnit.SECONDS).takeWhile { it <= duration }.subscribe {
      progressUpdate?.progress(it.toInt(), duration)
    }
  }

  fun update(position: Int, duration: Int) {
    subscription = Observable.interval(1, TimeUnit.SECONDS).map {
      position + it
    }.takeWhile {
      it <= duration
    }.observeOn(scheduler).subscribe {
      progressUpdate?.progress(it.toInt(), duration)
    }
  }

  fun stop() {
    subscription?.unsubscribe()
  }

  fun setProgressListener(progressUpdate: ProgressUpdate?) {
    this.progressUpdate = progressUpdate
  }

  interface ProgressUpdate {
    fun progress(position: Int, duration: Int)
  }

}
