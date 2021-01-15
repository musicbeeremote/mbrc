package com.kelsos.mbrc.common.ui.extensions

import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kelsos.mbrc.R

fun RecyclerView.ViewHolder.string(@StringRes resId: Int): String {
  return this.itemView.context.getString(resId)
}

fun Fragment.snackbar(@StringRes resId: Int) {
  val contentView = requireActivity().findViewById<View>(android.R.id.content)
  Snackbar.make(contentView, resId, Snackbar.LENGTH_SHORT).show()
}

fun RecyclerView.animateIfEmpty(items: Int) {
  if (items == 0) {
    val resId = R.anim.layout_animation_from_bottom
    val animation = AnimationUtils.loadLayoutAnimation(this.context, resId)
    this.layoutAnimation = animation
  }
}
