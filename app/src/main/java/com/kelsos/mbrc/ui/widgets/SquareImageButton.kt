package com.kelsos.mbrc.ui.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.kelsos.mbrc.R

class SquareImageButton : AppCompatImageButton {

  private var useHeight: Boolean = false

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    with(context.obtainStyledAttributes(attrs, R.styleable.SquareImageButton)) {
      useHeight = attrs.getAttributeBooleanValue(
        R.styleable.SquareImageButton_sib_use_height,
        false
      )
      recycle()
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val dimen = if (useHeight) {
      measuredHeight
    } else {
      measuredWidth
    }
    setMeasuredDimension(dimen, dimen)
  }
}
