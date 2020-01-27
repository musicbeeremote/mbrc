package com.kelsos.mbrc.features.queue

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent

class QueueWorker(
  context: Context,
  params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

  override suspend fun doWork(): Result = coroutineScope {
    Result.success()
  }
}
