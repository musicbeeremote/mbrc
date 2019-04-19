package com.kelsos.mbrc.features.radio.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed

class RadioViewHolder(
  binding: ListitemSingleBinding,
  onViewItemPressed: OnViewItemPressed
) : BindableViewHolder<RadioStation>(binding) {
  private val name: TextView = binding.lineOne
  private val context: ImageView = binding.uiItemContextIndicator

  init {
    context.isVisible = false
    itemView.setOnClickListener { onViewItemPressed(bindingAdapterPosition) }
  }

  override fun bindTo(item: RadioStation) {
    name.text = item.name
  }

  override fun clear() {
    name.text = ""
  }

  companion object {
    fun create(parent: ViewGroup, onViewItemPressed: OnViewItemPressed): RadioViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      val binding = ListitemSingleBinding.inflate(inflater, parent, false)
      return RadioViewHolder(binding, onViewItemPressed)
    }
  }
}
