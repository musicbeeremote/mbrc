package com.kelsos.mbrc.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kelsos.mbrc.R
import com.kelsos.mbrc.extensions.getDimens
import com.squareup.picasso.Picasso
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay

private var clearDeferred: Deferred<Unit>? = null

@BindingAdapter("imageUrl")
fun ImageView.imageLoader(url: String) {
  val dimens = context.getDimens()

  clearDeferred?.cancel()

  if (url.isEmpty()) {
    clearDeferred = async(UI) {
      delay(800)
    }

    this.setImageResource(R.drawable.ic_image_no_cover)
    return
  }

  Picasso.get()
    .load(url)
    .noFade()
    .placeholder(R.drawable.ic_image_no_cover)
    .error(R.drawable.ic_image_no_cover)
    .config(Bitmap.Config.RGB_565)
    .resize(dimens, dimens)
    .centerCrop()
    .into(this)
}

@BindingAdapter("imageUrl", "error")
fun ImageView.imageLoader(
  url: String,
  error: Drawable
) {
  val dimens = context.getDimens()
  Picasso.get()
    .load(url)
    .noFade()
    .error(error)
    .placeholder(R.drawable.ic_image_no_cover)
    .config(Bitmap.Config.RGB_565)
    .resize(dimens, dimens)
    .centerCrop()
    .into(this)
}