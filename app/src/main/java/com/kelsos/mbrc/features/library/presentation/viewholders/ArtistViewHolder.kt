package com.kelsos.mbrc.features.library.presentation.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kelsos.mbrc.R
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.ui.BindableViewHolder

class ArtistViewHolder(
  binding: ListitemSingleBinding
) : BindableViewHolder<Artist>(binding) {
  private val title: TextView = binding.lineOne
  private val indicator: ImageView = binding.uiItemContextIndicator
  private val empty: String = itemView.context.getString(R.string.empty)

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

  fun onIndicatorClick(onClick: (view: View, position: Int) -> Unit) {
    indicator.setOnClickListener { onClick(it, bindingAdapterPosition) }
  }

  companion object {
    fun create(parent: ViewGroup): ArtistViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      val binding = ListitemSingleBinding.inflate(layoutInflater, parent, false)
      return ArtistViewHolder(binding)
    }
  }
}
