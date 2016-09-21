package com.kelsos.mbrc.events.ui

import android.graphics.Bitmap

class CoverChangedEvent(val cover: Bitmap? = null) {
  val available: Boolean
    get() = this.cover != null
}
