package com.kelsos.mbrc.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BindableViewHolder<in T>(
  binding: ViewBinding,
) : RecyclerView.ViewHolder(binding.root) {
  abstract fun bindTo(item: T)

  abstract fun clear()

  fun onPress(onPress: (position: Int) -> Unit) {
    itemView.setOnClickListener { onPress(bindingAdapterPosition) }
  }
}
