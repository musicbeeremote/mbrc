package com.kelsos.mbrc.ui

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BindableViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
  abstract fun bindTo(item: T)
  abstract fun clear()
}