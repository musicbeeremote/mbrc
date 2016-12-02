package com.kelsos.mbrc.annotations

import android.support.annotation.IntDef

class Connection {
  companion object {
    const val OFF = 0
    const val ON = 1
  }

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(OFF.toLong(), ON.toLong())
  annotation class Status
}
