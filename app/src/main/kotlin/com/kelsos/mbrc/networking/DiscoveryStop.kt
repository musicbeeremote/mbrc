package com.kelsos.mbrc.networking

import android.support.annotation.IntDef

object DiscoveryStop {
  const val NO_WIFI = 1
  const val NOT_FOUND = 2
  const val COMPLETE = 3

  @IntDef(NO_WIFI, NOT_FOUND, COMPLETE)
  @Retention(AnnotationRetention.SOURCE)
  annotation class Reason
}
