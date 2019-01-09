package com.kelsos.mbrc.networking.connections

import android.annotation.SuppressLint
import androidx.annotation.IntDef

object Connection {
  const val OFF = 0
  const val ON = 1
  const val ACTIVE = 2

  @SuppressLint("SwitchIntDef")
  fun string(@Status type: Int): String {
    return when (type) {
      OFF -> "off"
      ON -> "on"
      ACTIVE -> "active"
      else -> "$type is unknown"
    }
  }

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(
    OFF,
    ON,
    ACTIVE
  )
  annotation class Status
}