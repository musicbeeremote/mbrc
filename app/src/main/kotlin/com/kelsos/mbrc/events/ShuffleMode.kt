package com.kelsos.mbrc.events

import androidx.annotation.StringDef

object ShuffleMode {
  const val OFF = "off"
  const val AUTODJ = "autodj"
  const val SHUFFLE = "shuffle"

  @StringDef(OFF, AUTODJ, SHUFFLE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class Shuffle
}