package com.kelsos.mbrc.features.radio.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.ui.BindableViewHolder
import com.kelsos.mbrc.ui.OnViewItemPressed
import kotterknife.bindView

class RadioViewHolder(
  itemView: View,
  onViewItemPressed: OnViewItemPressed
) : BindableViewHolder<RadioStation>(itemView) {
  private val name: TextView by bindView(R.id.line_one)
  private val context: ImageView by bindView(R.id.ui_item_context_indicator)

  init {
    context.isVisible = false
    itemView.setOnClickListener { onViewItemPressed(adapterPosition) }
  }

  override fun bindTo(item: RadioStation) {
    name.text = item.name
  }

  override fun clear() {
    name.text = ""
  }

  companion object {
    fun create(parent: ViewGroup, onViewItemPressed: OnViewItemPressed): RadioViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val view = inflater.inflate(R.layout.listitem_single, parent, false)
      return RadioViewHolder(view, onViewItemPressed)
    }
  }
}