package com.kelsos.mbrc.content.active_status


object Repeat {

  const val ALL = "all"
  const val NONE = "none"
  const val ONE = "one"

  @android.support.annotation.StringDef(com.kelsos.mbrc.content.active_status.Repeat.ALL, com.kelsos.mbrc.content.active_status.Repeat.NONE, com.kelsos.mbrc.content.active_status.Repeat.ONE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class Mode

}
