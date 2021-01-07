package com.kelsos.mbrc.content.activestatus

import com.kelsos.mbrc.ui.navigation.player.LfmRating

data class TrackRating(
  val lfmRating: LfmRating = LfmRating.Normal,
  val rating: Float = 0f
) {
  fun isFavorite(): Boolean = lfmRating == LfmRating.Loved
}
