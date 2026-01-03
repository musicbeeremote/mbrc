package com.kelsos.mbrc.feature.widgets.glance

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition

/**
 * Normal size music widget (128dp height).
 * Shows album cover, track title, artist, album, and playback controls.
 */
class NormalWidget : GlanceAppWidget() {

  override val stateDefinition: GlanceStateDefinition<Preferences> = WidgetGlanceStateDefinition

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val prefs = currentState<Preferences>()
      val state = WidgetGlanceStateDefinition.preferencesToWidgetState(context, prefs)

      GlanceTheme {
        NormalWidgetContent(state)
      }
    }
  }

  companion object {
    /**
     * Updates all instances of the normal widget.
     */
    suspend fun updateAll(context: Context) {
      val manager = GlanceAppWidgetManager(context)
      val glanceIds = manager.getGlanceIds(NormalWidget::class.java)
      glanceIds.forEach { glanceId ->
        NormalWidget().update(context, glanceId)
      }
    }
  }
}
