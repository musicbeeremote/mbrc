package com.kelsos.mbrc.content.activestatus

object Repeat {

  const val ALL = "all"
  const val NONE = "none"
  const val ONE = "one"

  @androidx.annotation.StringDef(
    Repeat.ALL,
    Repeat.NONE,
    Repeat.ONE
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Mode
}