package com.kelsos.mbrc.features.library.presentation

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.popup
import com.kelsos.mbrc.features.queue.Queue

class GenreAdapter : LibraryAdapter<Genre, GenreViewHolder>(
  DIFF_CALLBACK
) {

  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_genre) {
      val action = when (it) {
        R.id.popup_genre_play -> Queue.Now
        R.id.popup_genre_artists -> Queue.Default
        R.id.popup_genre_queue_next -> Queue.Next
        R.id.popup_genre_queue_last -> Queue.Last
        else -> throw IllegalArgumentException("invalid menuItem id $it")
      }
      val listener = requireListener()

      getItem(position)?.run {
        listener.onMenuItemSelected(action, this)
      }
    }
  }

  private val pressed: (View, Int) -> Unit = { _, position ->
    val listener = requireListener()
    getItem(position)?.let {
      listener.onItemClicked(it)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
    return GenreViewHolder.create(
      parent,
      indicatorPressed,
      pressed
    )
  }

  override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
    val genre = getItem(holder.adapterPosition)
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
        return oldItem == newItem
      }
    }
  }
}
