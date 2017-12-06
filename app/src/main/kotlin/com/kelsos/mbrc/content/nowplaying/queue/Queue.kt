package com.kelsos.mbrc.content.nowplaying.queue

import android.support.annotation.StringDef

object Queue {
  @StringDef(NEXT, LAST, NOW, ADD_ALL)
  @Retention(AnnotationRetention.SOURCE)
  annotation class QueueType

  const val NEXT = "next"
  const val LAST = "last"
  const val NOW = "now"
  const val ADD_ALL = "add-all"

}
