package com.kelsos.mbrc.features.library

import androidx.annotation.IntDef

interface FastScrollingListener {
  fun onFastScrolling(@FastScrolling.State state: Int)
}

object FastScrolling {
  const val STARTED = 1
  const val STOPPED = 2

  @IntDef(STARTED, STOPPED)
  @Retention(AnnotationRetention.SOURCE)
  annotation class State
}