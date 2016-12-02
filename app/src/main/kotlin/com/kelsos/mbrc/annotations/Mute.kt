package com.kelsos.mbrc.annotations

import android.support.annotation.IntDef

object Mute {
  const val UNDEF = -1
  const val OFF = 0
  const val ON = 1

  @IntDef(ON.toLong(), OFF.toLong(), UNDEF.toLong())
  @Retention(AnnotationRetention.SOURCE)
  annotation class State
}
