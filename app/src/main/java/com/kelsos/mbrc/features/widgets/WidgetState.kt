package com.kelsos.mbrc.features.widgets

import android.graphics.Bitmap

/**
 * State data class for Glance widgets.
 * Contains all the information needed to render the widget UI.
 */
data class WidgetState(
  val title: String = "",
  val artist: String = "",
  val album: String = "",
  val coverBitmap: Bitmap? = null,
  val isPlaying: Boolean = false
)
