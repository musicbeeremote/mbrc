package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object Queue {
  @StringDef(NEXT, LAST, NOW)
  @Retention(AnnotationRetention.SOURCE)
  annotation class QueueType

  const val NEXT = "next"
  const val LAST = "last"
  const val NOW = "now"

}
