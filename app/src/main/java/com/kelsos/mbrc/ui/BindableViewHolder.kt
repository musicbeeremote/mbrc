package com.kelsos.mbrc.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindableViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
  abstract fun bindTo(item: T)
  abstract fun clear()
}
