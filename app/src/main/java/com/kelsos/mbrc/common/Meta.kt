package com.kelsos.mbrc.common

sealed class Meta(
  val id: Int,
) {
  object Genre : Meta(GENRE)

  object Artist : Meta(ARTIST)

  object Album : Meta(ALBUM)

  object Track : Meta(TRACK)

  companion object {
    const val GENRE = 1
    const val ARTIST = 2
    const val ALBUM = 3
    const val TRACK = 4

    fun fromId(id: Int): Meta =
      when (id) {
        GENRE -> Genre
        ARTIST -> Artist
        ALBUM -> Album
        TRACK -> Track
        else -> throw IllegalArgumentException("$id is not a recognised option")
      }
  }
}
