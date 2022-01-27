package com.kelsos.mbrc.features.library.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.kelsos.mbrc.R
import timber.log.Timber

class SyncWorker(
  context: Context,
  params: WorkerParameters,
  private val librarySyncUseCase: LibrarySyncUseCase,
  private val notificationManager: NotificationManager,
) : CoroutineWorker(context, params) {
  private fun createChannel(id: String) {
    val channel =
      NotificationChannel(
        id,
        applicationContext.getString(R.string.notification__actions_name),
        NotificationManager.IMPORTANCE_DEFAULT,
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

    createChannel(id)

    val builder =
      NotificationCompat
        .Builder(applicationContext, id)
        .setContentTitle(title)
        .setTicker(title)
        .setContentText(description)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .setOngoing(true)
    val notification = builder.build()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ForegroundInfo(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    } else {
      ForegroundInfo(NOTIFICATION_ID, notification)
    }
  }

  private fun updateProgress(
    current: Int,
    total: Int,
    category: Int,
  ) {
    val id = applicationContext.getString(R.string.notification__actions_id)
    val title = applicationContext.getString(R.string.notification__sync_title)
    val contextText =
      applicationContext.getString(
        when (category) {
          SyncCategory.GENRES -> R.string.notification__sync_genres
          SyncCategory.ALBUMS -> R.string.notification__sync_albums
          SyncCategory.ARTISTS -> R.string.notification__sync_artists
          SyncCategory.TRACKS -> R.string.notification__sync_tracks
          SyncCategory.PLAYLISTS -> R.string.notification__sync_playlists
          SyncCategory.COVERS -> R.string.notification__sync_covers
          else -> error("not supported")
        },
        current,
        total,
      )
    val builder =
      NotificationCompat
        .Builder(applicationContext, id)
        .setContentTitle(title)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .setOngoing(true)
        .setProgress(current, total, false)
        .setContentText(contextText)

    notificationManager.notify(NOTIFICATION_ID, builder.build())
  }

  override suspend fun doWork(): Result {
    val auto = inputData.getBoolean(AUTO, false)

    setForeground(createForegroundInfo())

    return when (
      librarySyncUseCase.sync(auto) { current: Int, total: Int, category: Int ->
        updateProgress(current, total, category)
        setProgress(
          workDataOf(
            CURRENT to current,
            TOTAL to total,
            CATEGORY to category,
          ),
        )
      }
    ) {
      SyncResult.SUCCESS,
      SyncResult.NOOP,
      -> Result.success()
      SyncResult.FAILED -> Result.failure()
    }
  }

  companion object {
    const val AUTO = "sync_auto"
    const val CURRENT = "current"
    const val TOTAL = "total"
    const val CATEGORY = "category"

    const val SYNC_WORK_TAG = "sync_work"

    const val NOTIFICATION_ID = 819

    fun createWorkRequest(auto: Boolean = false): OneTimeWorkRequest {
      Timber.i("Scheduling library sync")
      val input =
        Data
          .Builder()
          .putBoolean(AUTO, auto)
          .build()

      return OneTimeWorkRequestBuilder<SyncWorker>()
        .setInputData(input)
        .build()
    }
  }
}
