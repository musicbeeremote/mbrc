package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef


object Repeat {

  const val ALL = "all"
  const val NONE = "none"
  const val ONE = "one"

  @StringDef(ALL, NONE, ONE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class Mode

}
