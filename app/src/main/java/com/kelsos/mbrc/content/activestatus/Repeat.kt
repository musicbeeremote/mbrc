package com.kelsos.mbrc.content.activestatus

import androidx.annotation.StringDef

object Repeat {

  const val ALL = "all"
  const val NONE = "none"
  const val ONE = "one"

  @StringDef(
    ALL,
    NONE,
    ONE
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Mode
}
