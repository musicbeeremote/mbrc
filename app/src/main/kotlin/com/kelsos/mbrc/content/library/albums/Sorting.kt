package com.kelsos.mbrc.content.library.albums

import android.support.annotation.IntDef

object Sorting {
  const val ALBUM = 1
  const val ALBUM_ARTIST__ALBUM = 2
  const val ALBUM_ARTIST__YEAR__ALBUM = 3
  const val ARTIST__ALBUM = 4
  const val GENRE__ALBUM_ARTIST__ALBUM = 5
  const val YEAR__ALBUM = 6
  const val YEAR__ALBUM_ARTIST__ALBUM = 7

  const val ORDER_ASCENDING = 1
  const val ORDER_DESCENDING = 2

  @IntDef(ORDER_ASCENDING, ORDER_DESCENDING)
  @Retention(AnnotationRetention.SOURCE)
  annotation class Order

  /**
   * There order in which the albums appear sorted to the user.
   */
  @IntDef(
    ALBUM,
    ALBUM_ARTIST__ALBUM,
    ALBUM_ARTIST__YEAR__ALBUM,
    ARTIST__ALBUM,
    GENRE__ALBUM_ARTIST__ALBUM,
    YEAR__ALBUM,
    YEAR__ALBUM_ARTIST__ALBUM
  )
  @Retention(AnnotationRetention.SOURCE)
  annotation class Fields
}