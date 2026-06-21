package com.kelsos.mbrc.feature.widgets.glance

import androidx.glance.appwidget.GlanceAppWidget

/**
 * Receiver for the normal size music widget.
 * This is the entry point registered in AndroidManifest.xml.
 */
class NormalWidgetReceiver : MusicWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = NormalWidget()
}
