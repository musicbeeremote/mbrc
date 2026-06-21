package com.kelsos.mbrc.feature.widgets.glance

import androidx.glance.appwidget.GlanceAppWidget

/**
 * Receiver for the small size music widget.
 * This is the entry point registered in AndroidManifest.xml.
 */
class SmallWidgetReceiver : MusicWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = SmallWidget()
}
