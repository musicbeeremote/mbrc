package com.kelsos.mbrc.features.queue

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
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta
import kotlinx.coroutines.coroutineScope

class QueueWorker(
  context: Context,
  params: WorkerParameters,
  private val queueUseCase: QueueUseCase
) : CoroutineWorker(context, params) {

  private val notificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as
      NotificationManager

  override suspend fun doWork(): Result = coroutineScope {

    val id = inputData.getLong(ID, -1)
    val meta = Meta.fromId(inputData.getInt(META, -1))
    val action = Queue.fromString(inputData.getString(ACTION) ?: Queue.DEFAULT)

    setForeground(createForegroundInfo())

    queueUseCase.queue(id, meta, action).fold(
      { Result.failure() },
      { status ->
        if (status == 200) {
          Result.success()
        } else {
          Result.failure()
        }
      }
    )
  }

  private fun createForegroundInfo(): ForegroundInfo {
    val id = applicationContext.getString(R.string.notification__actions_id)
    val title = applicationContext.getString(R.string.notification__queue_title)
    val description = applicationContext.getString(R.string.notification__queue_description)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannel(id)
    }

    val notification = NotificationCompat.Builder(applicationContext, id)
      .setContentTitle(title)
      .setTicker(title)
      .setContentText(description)
      .setSmallIcon(R.drawable.ic_mbrc_status)
      .setOngoing(true)
      .build()

    return ForegroundInfo(819, notification)
  }

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

  companion object {
    const val ID = "queue_id"
    const val META = "queue_meta"
    const val ACTION = "queue_action"

    fun createWorkRequest(
      id: Long,
      meta: Meta,
      action: Queue = Queue.Default
    ): OneTimeWorkRequest {

      val input = Data.Builder()
        .putLong(ID, id)
        .putInt(META, meta.id)
        .putString(ACTION, action.action)
        .build()

      return OneTimeWorkRequestBuilder<QueueWorker>()
        .setInputData(input)
        .build()
    }
  }
}
