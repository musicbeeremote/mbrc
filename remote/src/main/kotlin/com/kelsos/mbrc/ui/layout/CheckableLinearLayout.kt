package com.kelsos.mbrc.ui.layout

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.CheckedTextView
import android.widget.LinearLayout
import com.kelsos.mbrc.R

class CheckableLinearLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), Checkable {

  private var mCheckedTextView: CheckedTextView? = null

  override fun onFinishInflate() {
    super.onFinishInflate()
    val childCount = childCount
    for (i in 0..childCount - 1) {
      val view = getChildAt(i)
      if (view is CheckedTextView) {
        mCheckedTextView = view
      }
    }
  }

  /**
   * @return The current checked state of the view
   */
  override fun isChecked(): Boolean {
    return mCheckedTextView != null && mCheckedTextView!!.isChecked
  }

  /**
   * Change the checked state of the view

   * @param checked The new checked state
   */
  override fun setChecked(checked: Boolean) {
    if (mCheckedTextView != null) {
      mCheckedTextView!!.isChecked = checked
      changeColor(checked)
    }
  }

  /**
   * Change the checked state of the view to the inverse of its current state
   */
  override fun toggle() {
    if (mCheckedTextView != null) {
      mCheckedTextView!!.toggle()
      changeColor(mCheckedTextView!!.isChecked)
    }
  }

  private fun changeColor(isChecked: Boolean) {
    if (isChecked) {
      setBackgroundColor(resources.getColor(R.color.mbrc_selected_item))
    } else {
      setBackgroundColor(resources.getColor(android.R.color.transparent))
    }
  }
}
