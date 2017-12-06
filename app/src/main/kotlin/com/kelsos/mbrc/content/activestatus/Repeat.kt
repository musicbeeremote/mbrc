package com.kelsos.mbrc.content.activestatus


object Repeat {

  const val ALL = "all"
  const val NONE = "none"
  const val ONE = "one"

  @android.support.annotation.StringDef(com.kelsos.mbrc.content.activestatus.Repeat.ALL, com.kelsos.mbrc.content.activestatus.Repeat.NONE, com.kelsos.mbrc.content.activestatus.Repeat.ONE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class Mode

}
