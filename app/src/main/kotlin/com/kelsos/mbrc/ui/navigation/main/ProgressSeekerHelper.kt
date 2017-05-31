package com.kelsos.mbrc.ui.navigation.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ProgressSeekerHelper
@Inject
constructor() {
  private var progressUpdate: ProgressUpdate? = null
  private val job = SupervisorJob()
  private val scope = CoroutineScope(job + Dispatchers.IO)
  private var progressJob: Job? = null

  fun start(duration: Int) {
    stop()
    startUpdating(0, duration)
  }

  private fun startUpdating(position: Int, duration: Int) {
    progressJob = flow<Int> {
      repeat(duration.minus(position).floorDiv(1000)) { current ->
        delay(1000)
        try {
          progressUpdate?.progress(current * 1000, duration)
        } catch (e: Exception) {
          Timber.e(e, "Error while updating progress")
        }
      }
    }.launchIn(scope)
  }

  fun update(position: Int, duration: Int) {
    stop()
    startUpdating(position, duration)
  }

  fun stop() {
    scope.launch { progressJob?.cancelAndJoin() }
  }

  fun setProgressListener(progressUpdate: ProgressUpdate?) {
    this.progressUpdate = progressUpdate
  }

  interface ProgressUpdate {
    fun progress(position: Int, duration: Int)
  }
}
