package com.kelsos.mbrc.ui.navigation.player

import android.widget.RatingBar
import androidx.databinding.BindingAdapter

@BindingAdapter(
  value = ["onRatingChangeByUser"]
)
fun RatingBar.setOnRatingBarChangeListener(
  listener: OnRatingChangedByUserListener
) {
  onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
    if (!fromUser) {
      return@OnRatingBarChangeListener
    }

    listener.onRatingChanged(rating)
  }
}

interface OnRatingChangedByUserListener {
  fun onRatingChanged(rating: Float)
}