package com.kelsos.mbrc.content.activestatus

import com.kelsos.mbrc.ui.navigation.player.LfmRating

data class TrackRating(
  @LfmRating.Rating
  val lfmRating: Int = LfmRating.NORMAL,
  val rating: Float = 0f
) {
  fun isFavorite(): Boolean = lfmRating == LfmRating.LOVED
}
