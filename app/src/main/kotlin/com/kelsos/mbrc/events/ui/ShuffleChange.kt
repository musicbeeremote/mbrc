package com.kelsos.mbrc.events.ui

import android.support.annotation.StringDef

class ShuffleChange(@ShuffleState val shuffleState: String) {

  @StringDef(OFF, AUTODJ, SHUFFLE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class ShuffleState

  companion object {
    const val OFF = "off"
    const val AUTODJ = "autodj"
    const val SHUFFLE = "shuffle"
  }
}
