package com.kelsos.mbrc.ui.navigation.main

import android.support.annotation.IntDef

object LfmRating {
  const val LOVED = 1
  const val BANNED = -1
  const val NORMAL = 0

  @IntDef(LOVED.toLong(), BANNED.toLong(), NORMAL.toLong())
  @Retention(AnnotationRetention.SOURCE)
  annotation class Rating
}
