package com.kelsos.mbrc.content.activestatus

object Repeat {

  const val ALL = "all"
  const val NONE = "none"
  const val ONE = "one"

  @Retention(AnnotationRetention.SOURCE)
  @androidx.annotation.StringDef(
    ALL,
    NONE,
    ONE
  )
  annotation class Mode
}