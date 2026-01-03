package com.kelsos.mbrc.adapters

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Intent
import com.kelsos.mbrc.core.platform.intents.AppLauncher
import com.kelsos.mbrc.core.platform.intents.MediaIntentActions
import com.kelsos.mbrc.core.platform.intents.MediaIntentBuilder
import com.kelsos.mbrc.core.platform.mediasession.RemoteIntentCode
import com.kelsos.mbrc.ui.MainActivity

class RemoteViewIntentBuilderImpl :
  MediaIntentBuilder,
  AppLauncher {

  override fun getPendingIntent(
    remoteIntentCode: RemoteIntentCode,
    context: Context
  ): PendingIntent {
    val code = remoteIntentCode.code
    val flag = FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE

    val intent =
      when (remoteIntentCode) {
        RemoteIntentCode.Open -> Intent(context, MainActivity::class.java)
        RemoteIntentCode.Play -> Intent(MediaIntentActions.PLAY_PRESSED)
        RemoteIntentCode.Next -> Intent(MediaIntentActions.NEXT_PRESSED)
        RemoteIntentCode.Close -> Intent(MediaIntentActions.CLOSE_PRESSED)
        RemoteIntentCode.Previous -> Intent(MediaIntentActions.PREVIOUS_PRESSED)
        RemoteIntentCode.Cancel -> Intent(MediaIntentActions.CANCELLED_NOTIFICATION)
      }

    return if (remoteIntentCode == RemoteIntentCode.Open) {
      getActivity(context, code, intent, flag)
    } else {
      getBroadcast(context, code, intent, flag)
    }
  }

  override fun getLaunchPendingIntent(context: Context): PendingIntent {
    val flag = FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
    val intent = Intent(context, MainActivity::class.java)
    return getActivity(context, RemoteIntentCode.Open.code, intent, flag)
  }
}
