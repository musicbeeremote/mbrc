package com.kelsos.mbrc.content.active_status

import android.support.annotation.StringDef

object PlayerState {
  const val PLAYING = "playing"
  const val PAUSED = "paused"
  const val STOPPED = "stopped"
  const val UNDEFINED = "undefined"

  @Retention(AnnotationRetention.SOURCE)
  @StringDef(PAUSED, PLAYING, STOPPED, UNDEFINED)
  annotation class State
}//no instance
