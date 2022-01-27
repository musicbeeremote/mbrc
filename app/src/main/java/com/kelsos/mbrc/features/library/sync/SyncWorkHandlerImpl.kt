package com.kelsos.mbrc.features.library.sync

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.kelsos.mbrc.features.library.sync.SyncWorker.Companion.CATEGORY
import com.kelsos.mbrc.features.library.sync.SyncWorker.Companion.CURRENT
import com.kelsos.mbrc.features.library.sync.SyncWorker.Companion.TOTAL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SyncWorkHandlerImpl(
  private val workManager: WorkManager,
) : SyncWorkHandler {
  override fun sync(auto: Boolean) {
    val workRequest = SyncWorker.createWorkRequest(auto)
    workManager.enqueueUniqueWork(SyncWorker.SYNC_WORK_TAG, ExistingWorkPolicy.REPLACE, workRequest)
  }

  override fun syncProgress(): Flow<LibrarySyncProgress> {
    return workManager.getWorkInfosForUniqueWorkFlow(SyncWorker.SYNC_WORK_TAG).map {
      if (it.isEmpty()) {
        return@map LibrarySyncProgress(0, 0, 0, false)
      }
      val workInfo = it.first()
      val progress = workInfo.progress
      return@map LibrarySyncProgress(
        progress.getInt(CURRENT, 0),
        progress.getInt(TOTAL, 0),
        progress.getInt(CATEGORY, 0),
        workInfo.state === WorkInfo.State.RUNNING,
      )
    }
  }
}
