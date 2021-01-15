package com.kelsos.mbrc.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.getDimens
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var coroutineContext = Job() + Dispatchers.Main

@BindingAdapter("imageUrl")
fun ImageView.imageLoader(url: String?) {
  val dimens = context.getDimens()

  coroutineContext.cancelChildren()

  if (url.isNullOrBlank()) {
    CoroutineScope(coroutineContext).launch {
      delay(800)
    }

    this.setImageResource(R.drawable.ic_image_no_cover)
    return
  }

  Picasso.get()
    .load(url)
    .noPlaceholder()
    .error(R.drawable.ic_image_no_cover)
    .config(Bitmap.Config.RGB_565)
    .resize(dimens, dimens)
    .centerCrop()
    .into(this)
}

@BindingAdapter("imageUrl", "error")
fun ImageView.imageLoader(
  url: String?,
  error: Drawable
) {
  val dimens = context.getDimens()
  Picasso.get()
    .load(url)
    .noPlaceholder()
    .error(error)
    .config(Bitmap.Config.RGB_565)
    .resize(dimens, dimens)
    .centerCrop()
    .into(this)
}
