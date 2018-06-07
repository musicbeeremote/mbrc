package com.kelsos.mbrc.extensions

import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView

fun androidx.recyclerview.widget.RecyclerView.ViewHolder.string(@StringRes resId: Int): String {
  return this.itemView.context.getString(resId)
}