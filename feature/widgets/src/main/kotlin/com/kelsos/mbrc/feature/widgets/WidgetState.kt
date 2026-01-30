package com.kelsos.mbrc.feature.widgets

import android.graphics.Bitmap
import androidx.compose.runtime.Stable

/**
 * State data class for Glance widgets.
 * Contains all the information needed to render the widget UI.
 */
@Stable
data class WidgetState(
  val title: String = "",
  val artist: String = "",
  val album: String = "",
  val coverBitmap: Bitmap? = null,
  val isPlaying: Boolean = false
)
