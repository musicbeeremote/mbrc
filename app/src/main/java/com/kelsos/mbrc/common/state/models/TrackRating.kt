package com.kelsos.mbrc.common.state.models

import com.kelsos.mbrc.features.player.LfmRating

data class TrackRating(
  val lfmRating: LfmRating = LfmRating.Normal,
  val rating: Float = 0f
) {
  fun isFavorite(): Boolean = lfmRating == LfmRating.Loved
}
