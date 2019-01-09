package com.kelsos.mbrc.metrics

interface SyncMetrics {
  fun librarySyncStarted()
  fun librarySyncComplete(metrics: SyncedData)
  fun librarySyncFailed()
}
