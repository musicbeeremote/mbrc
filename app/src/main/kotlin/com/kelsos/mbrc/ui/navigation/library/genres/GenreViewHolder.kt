package com.kelsos.mbrc.ui.navigation.library.genres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.Group
import androidx.core.view.isVisible
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.extensions.string
import com.kelsos.mbrc.ui.BindableViewHolder
import kotterknife.bindView

class GenreViewHolder(
  itemView: View,
  indicatorPressed: (view: View, position: Int) -> Unit,
  pressed: (view: View, position: Int) -> Unit
) : BindableViewHolder<GenreEntity>(itemView) {
  private val title: TextView by bindView(R.id.line_one)
  private val indicator: ImageView by bindView(R.id.ui_item_context_indicator)
  private val loading: Group by bindView(R.id.listitem_loading)
  private val empty: String by lazy { string(R.string.empty) }

  init {
    indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
    itemView.setOnClickListener { pressed(it, adapterPosition) }
  }

  override fun bindTo(item: GenreEntity) {
    loading.isVisible = false
    title.text = if (item.genre.isBlank()) empty else item.genre
  }

  override fun clear() {
    loading.isVisible = true
    title.text = ""
  }

  companion object {
    fun create(
      parent: ViewGroup,
      indicatorPressed: (view: View, position: Int) -> Unit,
      pressed: (view: View, position: Int) -> Unit
    ): GenreViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val view = inflater.inflate(R.layout.listitem_single, parent, false)
      return GenreViewHolder(view, indicatorPressed, pressed)
    }
  }
}