package com.kelsos.mbrc.features.library.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.kelsos.mbrc.R
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class SyncWorker(
  context: Context,
  params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

  private val librarySyncUseCase: LibrarySyncUseCase by inject()
  private val notificationManager: NotificationManager by inject()

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createChannel(id: String) {
    val channel = NotificationChannel(
      id,
      applicationContext.getString(R.string.notification__actions_name),
      NotificationManager.IMPORTANCE_DEFAULT
    )

    channel.apply {
      this.description = applicationContext.getString(R.string.notification__actions_description)
      enableLights(false)
      enableVibration(false)
      setSound(null, null)
    }

    notificationManager.createNotificationChannel(channel)
  }

  private fun createForegroundInfo(): ForegroundInfo {
    val id = applicationContext.getString(R.string.notification__actions_id)
    val title = applicationContext.getString(R.string.notification__sync_title)
    val description = applicationContext.getString(R.string.notification__sync_description)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannel(id)
    }

    val builder = NotificationCompat.Builder(applicationContext, id)
      .setContentTitle(title)
      .setTicker(title)
      .setContentText(description)
      .setSmallIcon(R.drawable.ic_mbrc_status)
      .setOngoing(true)
    val notification = builder
      .build()

    return ForegroundInfo(819, notification)
  }

  private fun updateProgress(current: Int, total: Int, category: Int) {
    val id = applicationContext.getString(R.string.notification__actions_id)
    val title = applicationContext.getString(R.string.notification__sync_title)
    val builder = NotificationCompat.Builder(applicationContext, id)
      .setContentTitle(title)
      .setSmallIcon(R.drawable.ic_mbrc_status)
      .setOngoing(true)
      .setProgress(current, total, false)

    notificationManager.notify(819, builder.build())
  }

  override suspend fun doWork(): Result {
    val auto = inputData.getBoolean(AUTO, false)

    setForeground(createForegroundInfo())

    return when (librarySyncUseCase.sync(auto) { current: Int, total: Int, category: Int ->
      updateProgress(current, total, category)
      setProgress(workDataOf(
        CURRENT to current,
        TOTAL to total,
        CATEGORY to category
      ))
    }) {
      SyncResult.SUCCESS,
      SyncResult.NOOP -> Result.success()
      SyncResult.FAILED -> Result.failure()
    }
  }

  companion object {
    const val AUTO = "sync_auto"
    const val CURRENT = "current"
    const val TOTAL = "total"
    const val CATEGORY = "category"

    fun createWorkRequest(
      auto: Boolean = false
    ): OneTimeWorkRequest {

      Timber.i("Scheduling library sync")
      val input = Data.Builder()
        .putBoolean(AUTO, auto)
        .build()

      return OneTimeWorkRequestBuilder<SyncWorker>()
        .setInputData(input)
        .build()
    }
  }
}