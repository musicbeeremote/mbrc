package com.kelsos.mbrc.metrics

class DummySyncMetrics : SyncMetrics {

  override fun librarySyncComplete(metrics: SyncedData) {
  }

  override fun librarySyncStarted() {
  }

  override fun librarySyncFailed() {
  }
}