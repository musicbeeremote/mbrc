package com.kelsos.mbrc.features.library.presentation.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.popup
import com.kelsos.mbrc.features.library.presentation.viewholders.GenreViewHolder

class GenreAdapter : LibraryAdapter<Genre, GenreViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
    val holder = GenreViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_genre) { id ->
        val genre = getItem(position) ?: return@popup
        requireListener().onMenuItemSelected(id, genre)
      }
    }
    holder.onPress { position ->
      val genre = getItem(position) ?: return@onPress
      requireListener().onItemClicked(genre)
    }
    return holder
  }

  override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
    val genre = getItem(holder.bindingAdapterPosition)
    if (genre != null) {
      holder.bindTo(genre)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Genre>() {
      override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
        return oldItem.genre == newItem.genre
      }
    }
  }
}
