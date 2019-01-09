package com.kelsos.mbrc.ui.navigation.player

import android.widget.RatingBar

inline fun RatingBar.setOnRatingBarChangeListener(
  crossinline onRatingChanged: (rating: Float) -> Unit
) {
  onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
    if (!fromUser) {
      return@OnRatingBarChangeListener
    }

    onRatingChanged(rating)
  }
}
