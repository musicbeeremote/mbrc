package com.kelsos.mbrc.annotations

import android.support.annotation.IntDef

object Search {
  const val SECTION_GENRE = 0
  const val SECTION_ARTIST = 1
  const val SECTION_ALBUM = 2
  const val SECTION_TRACK = 3

  @IntDef(SECTION_GENRE.toLong(), SECTION_ARTIST.toLong(), SECTION_ALBUM.toLong(), SECTION_TRACK.toLong())
  @Retention(AnnotationRetention.SOURCE)
  annotation class Section
}
