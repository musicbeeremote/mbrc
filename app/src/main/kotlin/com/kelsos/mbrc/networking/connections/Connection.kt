package com.kelsos.mbrc.networking.connections

import android.support.annotation.IntDef

object Connection {
  const val OFF = 0
  const val ON = 1
  const val ACTIVE = 2

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(
    OFF,
    ON,
    ACTIVE
  )
  annotation class Status
}