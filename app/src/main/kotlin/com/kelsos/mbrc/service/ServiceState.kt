package com.kelsos.mbrc.service

/**
 * Singleton that tracks the foreground service state.
 */
object ServiceState {
  @Volatile
  var isRunning: Boolean = false
    private set

  @Volatile
  var isStopping: Boolean = false
    private set

  fun setRunning(running: Boolean) {
    isRunning = running
  }

  fun setStopping(stopping: Boolean) {
    isStopping = stopping
  }
}
