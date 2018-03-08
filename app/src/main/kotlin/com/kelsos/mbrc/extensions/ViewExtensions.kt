package com.kelsos.mbrc.extensions

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE

fun View?.hide() {
  this?.visibility = INVISIBLE
}

fun View?.show() {
  this?.visibility = VISIBLE
}

fun View?.gone() {
  this?.visibility = GONE
}

fun View?.isInvisible(): Boolean = this?.visibility == INVISIBLE

fun RecyclerView.ViewHolder.string(@StringRes resId: Int): String {
  return this.itemView.context.getString(resId)
}