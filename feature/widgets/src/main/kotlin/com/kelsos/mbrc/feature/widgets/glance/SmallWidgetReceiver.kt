package com.kelsos.mbrc.feature.widgets.glance

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Receiver for the small size music widget.
 * This is the entry point registered in AndroidManifest.xml.
 */
class SmallWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = SmallWidget()
}
