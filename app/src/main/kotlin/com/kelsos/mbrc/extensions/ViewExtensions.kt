package com.kelsos.mbrc.extensions

import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView

fun RecyclerView.ViewHolder.string(@StringRes resId: Int): String {
  return this.itemView.context.getString(resId)
}