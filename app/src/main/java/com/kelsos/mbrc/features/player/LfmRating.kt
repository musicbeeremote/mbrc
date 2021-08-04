package com.kelsos.mbrc.features.player

sealed class LfmRating {
  object Loved : LfmRating()
  object Banned : LfmRating()
  object Normal : LfmRating()

  companion object {
    private const val LOVE = "Love"
    private const val BAN = "Ban"

    fun fromString(value: String?): LfmRating = when (value) {
      LOVE -> Loved
      BAN -> Banned
      else -> Normal
    }
  }
}
