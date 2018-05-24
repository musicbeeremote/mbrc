package com.kelsos.mbrc.ui.navigation.library

import android.support.annotation.IntDef

data class SyncProgress(
  val current: Int,
  val total: Int,
  @Type val type: Int
) {

  override fun toString(): String {
    return "$current of $total -- ($type)"
  }

  @IntDef(
    NONE,
    GENRE,
    ARTIST,
    ALBUM,
    TRACK
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Type

  companion object {
    const val NONE = 0
    const val GENRE = 1
    const val ARTIST = 2
    const val ALBUM = 3
    const val TRACK = 4
  }
}