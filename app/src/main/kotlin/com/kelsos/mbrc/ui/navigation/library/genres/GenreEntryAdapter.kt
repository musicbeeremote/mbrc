package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.recyclerview.widget.DiffUtil
import android.view.View
import android.view.ViewGroup
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.ui.FastScrollableAdapter
import com.kelsos.mbrc.ui.navigation.library.popup
import javax.inject.Inject

class GenreEntryAdapter
@Inject
constructor() : FastScrollableAdapter<GenreEntity, GenreViewHolder>(DIFF_CALLBACK) {

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
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GenreEntity>() {
      override fun areItemsTheSame(oldItem: GenreEntity, newItem: GenreEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: GenreEntity, newItem: GenreEntity): Boolean {
        return oldItem == newItem
      }
    }
  }
}