package com.kelsos.mbrc.platform.mediasession

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Intent
import android.support.annotation.IntDef
import com.kelsos.mbrc.ui.navigation.main.MainActivity

object RemoteViewIntentBuilder {
  const val PLAY_PRESSED = "com.kelsos.mbrc.notification.play"
  const val NEXT_PRESSED = "com.kelsos.mbrc.notification.next"
  const val CLOSE_PRESSED = "com.kelsos.mbrc.notification.close"
  const val PREVIOUS_PRESSED = "com.kelsos.mbrc.notification.previous"
  const val CANCELLED_NOTIFICATION = "com.kelsos.mbrc.notification.cancel"

  const val OPEN = 0
  const val PLAY = 1
  const val NEXT = 2
  const val CLOSE = 3
  const val PREVIOUS = 4
  const val CANCEL = 5

  @SuppressLint("SwitchIntDef")
  fun getPendingIntent(@ButtonAction id: Int, context: Context): PendingIntent {
    return when (id) {
      OPEN -> getActivity(
        context,
        OPEN,
        Intent(context, MainActivity::class.java),
        FLAG_UPDATE_CURRENT
      )
      PLAY -> getBroadcast(context, PLAY, Intent(PLAY_PRESSED), FLAG_UPDATE_CURRENT)
      NEXT -> getBroadcast(context, NEXT, Intent(NEXT_PRESSED), FLAG_UPDATE_CURRENT)
      CLOSE -> getBroadcast(context, CLOSE, Intent(CLOSE_PRESSED), FLAG_UPDATE_CURRENT)
      PREVIOUS -> getBroadcast(context, PREVIOUS, Intent(PREVIOUS_PRESSED), FLAG_UPDATE_CURRENT)
      CANCEL -> getBroadcast(context, CANCEL, Intent(CANCELLED_NOTIFICATION), FLAG_UPDATE_CURRENT)
      else -> throw IndexOutOfBoundsException()
    }
  }

  @IntDef(
    OPEN,
    PLAY,
    CLOSE,
    PREVIOUS,
    NEXT,
    CANCEL
  )
  @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
  annotation class ButtonAction
}