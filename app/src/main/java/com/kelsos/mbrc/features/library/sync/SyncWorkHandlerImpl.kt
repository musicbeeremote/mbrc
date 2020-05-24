package com.kelsos.mbrc.features.library.sync

import androidx.work.WorkManager

class SyncWorkHandlerImpl(private val workManager: WorkManager) : SyncWorkHandler {
  override fun sync(auto: Boolean) {
    workManager.enqueue(SyncWorker.createWorkRequest(auto))
  }
}
