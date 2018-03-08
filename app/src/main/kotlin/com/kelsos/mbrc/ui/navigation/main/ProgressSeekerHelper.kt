package com.kelsos.mbrc.ui.navigation.main

import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProgressSeekerHelper
@Inject constructor(
  private val schedulerProvider: SchedulerProvider
) {
  private var progressUpdate: ProgressUpdate? = null
  private var disposable: Disposable? = null

  fun start(duration: Int) {
    stop()
    disposable = Observable.interval(1, TimeUnit.SECONDS).takeWhile {
      it <= duration
    }.subscribe({
      progressUpdate?.progress(it.toInt(), duration)
    }) { onError(it) }
  }

  private fun onError(throwable: Throwable) {
    Timber.v(throwable, "Error on progress observable")
  }

  fun update(position: Int, duration: Int) {
    stop()
    disposable = Observable.interval(1, TimeUnit.SECONDS).map {
      position + it
    }.takeWhile {
      it <= duration
    }.observeOn(schedulerProvider.main()).subscribe({
      progressUpdate?.progress(it.toInt(), duration)
    }) { onError(it) }
  }

  fun stop() {
    disposable?.dispose()
  }

  fun setProgressListener(progressUpdate: ProgressUpdate?) {
    this.progressUpdate = progressUpdate
  }

  interface ProgressUpdate {
    fun progress(position: Int, duration: Int)
  }
}