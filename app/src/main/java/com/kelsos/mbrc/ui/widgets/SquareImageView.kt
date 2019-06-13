package com.kelsos.mbrc.ui.widgets

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.getDimens
import com.squareup.picasso.Picasso
import java.io.File

class SquareImageView : AppCompatImageView {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    setMeasuredDimension(measuredHeight, measuredHeight)
  }

  fun loadImage(coverUrl: String) {
    val dimens = context.getDimens()

    if (coverUrl.isEmpty()) {
      this.setImageResource(R.drawable.ic_image_no_cover)
      return
    }

    val coverFile = File(coverUrl)

    Picasso.get()
      .load(coverFile)
      .noFade()
      .placeholder(R.drawable.ic_image_no_cover)
      .error(R.drawable.ic_image_no_cover)
      .config(Bitmap.Config.RGB_565)
      .resize(dimens, dimens)
      .centerCrop()
      .into(this)
  }
}
