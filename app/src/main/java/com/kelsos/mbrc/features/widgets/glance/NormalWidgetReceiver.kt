package com.kelsos.mbrc.features.widgets.glance

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Receiver for the normal size music widget.
 * This is the entry point registered in AndroidManifest.xml.
 */
class NormalWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = NormalWidget()
}
