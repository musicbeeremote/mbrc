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
 * Small size music widget (72dp height).
 * Shows album cover, combined title-artist, and playback controls.
 */
class SmallWidget : GlanceAppWidget() {

  override val stateDefinition: GlanceStateDefinition<Preferences> = WidgetGlanceStateDefinition

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      val prefs = currentState<Preferences>()
      val state = WidgetGlanceStateDefinition.preferencesToWidgetState(context, prefs)

      GlanceTheme {
        SmallWidgetContent(state)
      }
    }
  }

  companion object {
    /**
     * Updates all instances of the small widget.
     */
    suspend fun updateAll(context: Context) {
      val manager = GlanceAppWidgetManager(context)
      val glanceIds = manager.getGlanceIds(SmallWidget::class.java)
      glanceIds.forEach { glanceId ->
        SmallWidget().update(context, glanceId)
      }
    }
  }
}
