package com.kelsos.mbrc.common

import androidx.annotation.IntDef

object Meta {
  @IntDef(
    NONE,
    GENRE,
    ARTIST,
    ALBUM,
    TRACK
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Type

  const val NONE = 0
  const val GENRE = 1
  const val ARTIST = 2
  const val ALBUM = 3
  const val TRACK = 4
}
