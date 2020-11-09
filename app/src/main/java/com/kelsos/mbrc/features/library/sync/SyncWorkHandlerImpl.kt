package com.kelsos.mbrc.features.library.sync

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.kelsos.mbrc.features.library.sync.SyncWorker.Companion.CATEGORY
import com.kelsos.mbrc.features.library.sync.SyncWorker.Companion.CURRENT
import com.kelsos.mbrc.features.library.sync.SyncWorker.Companion.TOTAL

class SyncWorkHandlerImpl(private val workManager: WorkManager) : SyncWorkHandler {
  override fun sync(auto: Boolean) {
    val workRequest = SyncWorker.createWorkRequest(auto)
    workManager.enqueueUniqueWork(SyncWorker.SYNC_WORK_TAG, ExistingWorkPolicy.REPLACE, workRequest)
  }

  override fun syncProgress(): LiveData<LibrarySyncProgress> {
    return Transformations.map(
      workManager.getWorkInfosForUniqueWorkLiveData(
        SyncWorker.SYNC_WORK_TAG
      )
    ) {
      val workInfo = it.first()
      val progress = workInfo.progress
      return@map LibrarySyncProgress(
        progress.getInt(CURRENT, 0),
        progress.getInt(TOTAL, 0),
        progress.getInt(CATEGORY, 0),
        workInfo.state === WorkInfo.State.RUNNING
      )
    }
  }
}
