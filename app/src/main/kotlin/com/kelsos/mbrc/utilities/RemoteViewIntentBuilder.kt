package com.kelsos.mbrc.utilities

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.annotation.IntDef
import com.kelsos.mbrc.ui.activities.nav.MainActivity

object RemoteViewIntentBuilder {
  const val REMOTE_PLAY_PRESSED = "com.kelsos.mbrc.notification.play"
  const val REMOTE_NEXT_PRESSED = "com.kelsos.mbrc.notification.next"
  const val REMOTE_CLOSE_PRESSED = "com.kelsos.mbrc.notification.close"
  const val REMOTE_PREVIOUS_PRESSED = "com.kelsos.mbrc.notification.previous"
  const val CANCELLED_NOTIFICATION = "com.kelsos.mbrc.notification.cancel"
  const val OPEN = 0
  const val PLAY = 1
  const val NEXT = 2
  const val CLOSE = 3
  const val PREVIOUS = 4
  const val CANCEL = 5

  fun getPendingIntent(@ButtonAction id: Int, mContext: Context): PendingIntent {
    when (id) {
      OPEN -> {
        val notificationIntent = Intent(mContext, MainActivity::class.java)
        return PendingIntent.getActivity(mContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
      }
      PLAY -> {
        val playPressedIntent = Intent(REMOTE_PLAY_PRESSED)
        return PendingIntent.getBroadcast(mContext,
            1,
            playPressedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
      }
      NEXT -> {
        val mediaNextButtonIntent = Intent(REMOTE_NEXT_PRESSED)
        return PendingIntent.getBroadcast(mContext,
            2,
            mediaNextButtonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
      }
      CLOSE -> {
        val clearNotificationIntent = Intent(REMOTE_CLOSE_PRESSED)
        return PendingIntent.getBroadcast(mContext,
            3,
            clearNotificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
      }
      PREVIOUS -> {
        val mediaPreviousButtonIntent = Intent(REMOTE_PREVIOUS_PRESSED)
        return PendingIntent.getBroadcast(mContext,
            4,
            mediaPreviousButtonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
      }
      CANCEL -> {
        val cancelIntent = Intent(CANCELLED_NOTIFICATION)
        return PendingIntent.getBroadcast(mContext,
            4,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
      }
      else -> throw IndexOutOfBoundsException()
    }
  }

  @IntDef(OPEN.toLong(),
      PLAY.toLong(),
      CLOSE.toLong(),
      PREVIOUS.toLong(),
      NEXT.toLong(),
      CANCEL.toLong())
  @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
  annotation class ButtonAction
}
