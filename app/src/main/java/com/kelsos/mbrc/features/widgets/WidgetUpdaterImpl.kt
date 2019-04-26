package com.kelsos.mbrc.features.widgets

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Intent
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.features.widgets.WidgetUpdater.Companion.COVER
import com.kelsos.mbrc.features.widgets.WidgetUpdater.Companion.COVER_PATH
import com.kelsos.mbrc.features.widgets.WidgetUpdater.Companion.INFO
import com.kelsos.mbrc.features.widgets.WidgetUpdater.Companion.PLAYER_STATE
import com.kelsos.mbrc.features.widgets.WidgetUpdater.Companion.STATE
import com.kelsos.mbrc.features.widgets.WidgetUpdater.Companion.TRACK_INFO

class WidgetUpdaterImpl(
  private val app: Application
) : WidgetUpdater {

  private fun createIntent(clazz: Class<*>): Intent {
    val widgetUpdateIntent = Intent(app, clazz)
    widgetUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
    return widgetUpdateIntent
  }

  private fun Intent.payload(track: PlayingTrack): Intent {
    return putExtra(INFO, true).putExtra(TRACK_INFO, track)
  }

  private fun Intent.payload(path: String): Intent {
    return putExtra(COVER, true).putExtra(COVER_PATH, path)
  }

  private fun Intent.statePayload(@PlayerState.State state: String): Intent {
    return putExtra(STATE, true).putExtra(PLAYER_STATE, state)
  }

  override fun updatePlayingTrack(track: PlayingTrack) {
    val normalIntent = createIntent(WidgetNormal::class.java).payload(track)
    val smallIntent = createIntent(WidgetSmall::class.java).payload(track)
    broadcast(smallIntent, normalIntent)
  }

  override fun updatePlayState(state: String) {
    val normalIntent = createIntent(WidgetNormal::class.java).statePayload(state)
    val smallIntent = createIntent(WidgetSmall::class.java).statePayload(state)
    broadcast(smallIntent, normalIntent)
  }

  override fun updateCover(path: String) {
    val normalIntent = createIntent(WidgetNormal::class.java).payload(path)
    val smallIntent = createIntent(WidgetSmall::class.java).payload(path)
    broadcast(smallIntent, normalIntent)
  }

  private fun broadcast(smallIntent: Intent, normalIntent: Intent) = with(app) {
    sendBroadcast(smallIntent)
    sendBroadcast(normalIntent)
  }
}