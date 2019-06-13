package com.kelsos.mbrc.common.ui.extensions

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.kelsos.mbrc.R

fun RecyclerView.animateIfEmpty(items: Int) {
  if (items == 0) {
    val resId = R.anim.layout_animation_from_bottom
    val animation = AnimationUtils.loadLayoutAnimation(this.context, resId)
    this.layoutAnimation = animation
  }
}
