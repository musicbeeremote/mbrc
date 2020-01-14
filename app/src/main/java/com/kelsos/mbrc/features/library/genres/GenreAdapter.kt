package com.kelsos.mbrc.features.library.genres

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.queue.LibraryPopup
import com.kelsos.mbrc.ui.FastScrollableAdapter
import com.kelsos.mbrc.features.library.popup

class GenreAdapter : FastScrollableAdapter<Genre, GenreViewHolder>(DIFF_CALLBACK) {

  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_genre) {
      val action = when (it) {
        R.id.popup_genre_play -> LibraryPopup.NOW
        R.id.popup_genre_artists -> LibraryPopup.PROFILE
        R.id.popup_genre_queue_next -> LibraryPopup.NEXT
        R.id.popup_genre_queue_last -> LibraryPopup.LAST
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
    return GenreViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
    if (fastScrolling) {
      holder.clear()
      return
    }
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