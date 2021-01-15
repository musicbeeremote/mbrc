package com.kelsos.mbrc.features.library.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.ui.BindableViewHolder

class ArtistViewHolder(
  binding: ListitemSingleBinding,
  indicatorPressed: (View, Int) -> Unit,
  pressed: (View, Int) -> Unit
) : BindableViewHolder<Artist>(binding.root) {
  private val title: TextView = binding.lineOne
  private val indicator: ImageView = binding.uiItemContextIndicator
  private val empty: String = itemView.context.getString(R.string.empty)

  init {
    indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
    itemView.setOnClickListener { pressed(it, adapterPosition) }
  }

  companion object {
    fun create(
      parent: ViewGroup,
      indicatorPressed: (View, Int) -> Unit,
      pressed: (View, Int) -> Unit
    ): ArtistViewHolder {
      val inflater = LayoutInflater.from(parent.context)
      val binding: ListitemSingleBinding = DataBindingUtil.inflate(
        inflater,
        R.layout.listitem_single,
        parent,
        false
      )
      return ArtistViewHolder(
        binding,
        indicatorPressed,
        pressed
      )
    }
  }

  override fun bindTo(item: Artist) {
    title.text = if (item.artist.isBlank()) {
      empty
    } else {
      item.artist
    }
  }

  override fun clear() {
    title.text = ""
  }
}
