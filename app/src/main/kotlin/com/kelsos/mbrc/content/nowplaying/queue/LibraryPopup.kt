package com.kelsos.mbrc.content.nowplaying.queue

import android.support.annotation.StringDef

object LibraryPopup {
  @StringDef(
    PROFILE,
    NEXT,
    LAST,
    NOW,
    ADD_ALL
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Action

  const val PROFILE = "profile"
  const val NEXT = "next"
  const val LAST = "last"
  const val NOW = "now"
  const val ADD_ALL = "add-all"
}