package com.kelsos.mbrc.platform.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.TrackInfo

object UpdateWidgets {
  const val COVER = "com.kelsos.mbrc.platform.widgets.COVER"
  const val COVER_PATH = "com.kelsos.mbrc.platform.widgets.COVER_PATH"
  const val STATE = "com.kelsos.mbrc.platform.widgets.STATE"
  const val INFO = "com.kelsos.mbrc.platform.widgets.INFO"
  const val TRACK_INFO = "com.kelsos.mbrc.platform.widgets.TRACKINFO"
  const val PLAYER_STATE = "com.kelsos.mbrc.platform.widgets.PLAYER_STATE"

  fun updateCover(context: Context, path: String = "") {
    val normalIntent = getIntent(WidgetNormal::class.java, context)
        .putExtra(COVER, true)
        .putExtra(COVER_PATH, path)
    val smallIntent = getIntent(WidgetSmall::class.java, context)
        .putExtra(COVER, true)
        .putExtra(COVER_PATH, path)

    context.sendBroadcast(smallIntent)
    context.sendBroadcast(normalIntent)
  }

  fun updatePlaystate(context: Context, @State state: String) {
    val normalIntent = getIntent(WidgetNormal::class.java, context)
        .putExtra(STATE, true)
        .putExtra(PLAYER_STATE, state)

    val smallIntent = getIntent(WidgetSmall::class.java, context)
        .putExtra(STATE, true)
        .putExtra(PLAYER_STATE, state)

    context.sendBroadcast(smallIntent)
    context.sendBroadcast(normalIntent)
  }

  fun updateTrackInfo(context: Context, info: TrackInfo) {
    val normalIntent = getIntent(WidgetNormal::class.java, context)
        .putExtra(INFO, true)
        .putExtra(TRACK_INFO, info)

    val smallIntent = getIntent(WidgetSmall::class.java, context)
        .putExtra(INFO, true)
        .putExtra(TRACK_INFO, info)

    context.sendBroadcast(smallIntent)
    context.sendBroadcast(normalIntent)
  }

  private fun getIntent(clazz: Class<*>, context: Context): Intent {
    val widgetUpdateIntent = Intent(context, clazz)
    widgetUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    return widgetUpdateIntent
  }


}
