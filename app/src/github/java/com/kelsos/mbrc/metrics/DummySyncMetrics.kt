package com.kelsos.mbrc.metrics

import timber.log.Timber

class DummySyncMetrics : SyncMetrics {
  var start = 0L

  override fun librarySyncComplete(metrics: SyncedData) {
    val total = System.currentTimeMillis() - start
    Timber.v("Total sync time $total ms for the following $metrics")
  }

  override fun librarySyncStarted() {
    Timber.v("Sync metrics timer started")
    start = System.currentTimeMillis()
  }

  override fun librarySyncFailed() {
    Timber.v("Ignoring metrics due to failure")
  }
}
