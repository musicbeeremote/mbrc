package com.kelsos.mbrc.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.View

abstract class BindableViewHolder<in T>(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
  abstract fun bindTo(item: T)
  abstract fun clear()
}