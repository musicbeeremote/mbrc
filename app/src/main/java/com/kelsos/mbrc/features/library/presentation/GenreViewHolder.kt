package com.kelsos.mbrc.features.library.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.extensions.string
import com.kelsos.mbrc.databinding.ListitemSingleBinding
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.ui.BindableViewHolder

class GenreViewHolder(
  binding: ListitemSingleBinding,
  indicatorPressed: (view: View, position: Int) -> Unit,
  pressed: (view: View, position: Int) -> Unit
) : BindableViewHolder<Genre>(binding.root) {
  private val title: TextView = binding.lineOne
  private val indicator: ImageView = binding.uiItemContextIndicator
  private val empty: String by lazy { string(R.string.empty) }

  init {
    indicator.setOnClickListener { indicatorPressed(it, adapterPosition) }
    itemView.setOnClickListener { pressed(it, adapterPosition) }
  }

  override fun bindTo(item: Genre) {
    title.text = if (item.genre.isBlank()) empty else item.genre
  }

  override fun clear() {
    title.text = ""
  }

  companion object {
    fun create(
      parent: ViewGroup,
      indicatorPressed: (view: View, position: Int) -> Unit,
      pressed: (view: View, position: Int) -> Unit
    ): GenreViewHolder {
      val inflater: LayoutInflater = LayoutInflater.from(parent.context)
      val binding: ListitemSingleBinding = DataBindingUtil.inflate(
        inflater,
        R.layout.listitem_single,
        parent,
        false
      )
      return GenreViewHolder(
        binding,
        indicatorPressed,
        pressed
      )
    }
  }
}
