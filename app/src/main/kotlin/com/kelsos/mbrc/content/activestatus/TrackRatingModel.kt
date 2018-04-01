package com.kelsos.mbrc.content.activestatus

import com.kelsos.mbrc.ui.navigation.main.LfmRating

data class TrackRatingModel(
  @LfmRating.Rating
  val lfmRating: Int = LfmRating.NORMAL,
  val rating: Float = 0f
)