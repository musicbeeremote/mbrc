package com.kelsos.mbrc.annotations

import android.support.annotation.IntDef

object SocketAction {
  const val RESET = 1
  const val START = 2
  const val RETRY = 3
  const val TERMINATE = 4
  const val STOP = 5

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(RESET.toLong(), START.toLong(), RETRY.toLong(), TERMINATE.toLong(), STOP.toLong())
  annotation class Action
}
