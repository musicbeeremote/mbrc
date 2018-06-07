package com.kelsos.mbrc.ui.navigation.main

import androidx.annotation.IntDef

object LfmRating {
  const val LOVED = 1
  const val BANNED = -1
  const val NORMAL = 0

  @IntDef(
    LOVED,
    BANNED,
    NORMAL
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Rating
}