package com.kelsos.mbrc.ui.navigation.library.artists

import android.support.constraint.Group
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.ui.BindableViewHolder
import kotterknife.bindView

class ArtistViewHolder(
  itemView: View,
  indicatorPressed: (View, Int) -> Unit,
  pressed: (View, Int) -> Unit
) : BindableViewHolder<ArtistEntity>(itemView) {
  private val title: TextView by bindView(R.id.line_one)
  private val indicator: ImageView by bindView(R.id.ui_item_context_indicator)
  private val loading: Group by bindView(R.id.listitem_loading)
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
      val view = inflater.inflate(R.layout.listitem_single, parent, false)
      return ArtistViewHolder(view, indicatorPressed, pressed)
    }
  }

  override fun bindTo(item: ArtistEntity) {
    loading.isVisible = false
    title.text = if (item.artist.isBlank()) {
      empty
    } else {
      item.artist
    }
  }

  override fun clear() {
    loading.isVisible = true
    title.text = ""
  }
}