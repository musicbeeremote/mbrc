package com.kelsos.mbrc.features.work

import androidx.work.WorkManager
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.queue.QueueWorker

class WorkHandlerImpl(
  private val workManager: WorkManager,
) : WorkHandler {
  override fun queue(
    id: Long,
    meta: Meta,
    action: Queue,
  ) {
    val workRequest = QueueWorker.createWorkRequest(id, meta, action)
    workManager.enqueue(workRequest)
  }
}
