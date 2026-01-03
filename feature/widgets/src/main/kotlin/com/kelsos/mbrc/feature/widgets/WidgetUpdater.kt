package com.kelsos.mbrc.feature.widgets

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.platform.state.PlayingTrack
import com.kelsos.mbrc.feature.widgets.glance.NormalWidget
import com.kelsos.mbrc.feature.widgets.glance.SmallWidget
import com.kelsos.mbrc.feature.widgets.glance.WidgetGlanceStateDefinition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

interface WidgetUpdater {
  fun updatePlayingTrack(track: PlayingTrack)

  fun updatePlayState(state: PlayerState)
}

class WidgetUpdaterImpl(private val context: Context) : WidgetUpdater {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var lastCoverUrl: String? = null

  @Suppress("TooGenericExceptionCaught") // Widget updates should not crash the app
  override fun updatePlayingTrack(track: PlayingTrack) {
    scope.launch {
      try {
        Timber.v("Updating widget track: ${track.title} - ${track.artist}")

        // Load cover if URL changed
        if (track.coverUrl != lastCoverUrl) {
          WidgetGlanceStateDefinition.loadAndCacheCover(context, track.coverUrl)
          lastCoverUrl = track.coverUrl
        }

        // Update state for all widget instances
        updateAllWidgetStates { prefs ->
          WidgetGlanceStateDefinition.trackInfoUpdate(
            title = track.title,
            artist = track.artist,
            album = track.album,
            coverUrl = track.coverUrl
          )(prefs)
        }

        // Trigger widget updates
        updateAllWidgets()
      } catch (e: Exception) {
        Timber.e(e, "Failed to update widget track")
      }
    }
  }

  @Suppress("TooGenericExceptionCaught") // Widget updates should not crash the app
  override fun updatePlayState(state: PlayerState) {
    scope.launch {
      try {
        val isPlaying = state == PlayerState.Playing
        Timber.v("Updating widget play state: $isPlaying")

        // Update state for all widget instances
        updateAllWidgetStates { prefs ->
          WidgetGlanceStateDefinition.playStateUpdate(isPlaying)(prefs)
        }

        // Trigger widget updates
        updateAllWidgets()
      } catch (e: Exception) {
        Timber.e(e, "Failed to update widget play state")
      }
    }
  }

  private suspend fun updateAllWidgetStates(
    update: suspend (androidx.datastore.preferences.core.Preferences) ->
    androidx.datastore.preferences.core.Preferences
  ) {
    val manager = GlanceAppWidgetManager(context)

    // Update normal widgets
    manager.getGlanceIds(NormalWidget::class.java).forEach { glanceId ->
      updateAppWidgetState(context, WidgetGlanceStateDefinition, glanceId, update)
    }

    // Update small widgets
    manager.getGlanceIds(SmallWidget::class.java).forEach { glanceId ->
      updateAppWidgetState(context, WidgetGlanceStateDefinition, glanceId, update)
    }
  }

  @Suppress("TooGenericExceptionCaught") // Widget updates should not crash the app
  private suspend fun updateAllWidgets() {
    try {
      NormalWidget.updateAll(context)
      SmallWidget.updateAll(context)
    } catch (e: Exception) {
      Timber.e(e, "Failed to update Glance widgets")
    }
  }
}
