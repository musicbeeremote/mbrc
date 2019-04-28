package com.kelsos.mbrc.ui.navigation.library.albums

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.features.queue.LibraryPopup
import com.kelsos.mbrc.ui.FastScrollableAdapter
import com.kelsos.mbrc.ui.navigation.library.popup

class AlbumAdapter : FastScrollableAdapter<Album, AlbumViewHolder>(DIFF_CALLBACK) {

  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_album) {
      val action = when (it) {
        R.id.popup_album_tracks -> LibraryPopup.PROFILE
        R.id.popup_album_queue_next -> LibraryPopup.NEXT
        R.id.popup_album_queue_last -> LibraryPopup.LAST
        R.id.popup_album_play -> LibraryPopup.NOW
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
    getItem(position)?.run {
      listener.onItemClicked(this)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
    return AlbumViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
    if (fastScrolling) {
      holder.clear()
      return
    }

    val album = getItem(position)

    if (album != null) {
      holder.bindTo(album)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Album>() {
      override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
        return oldItem == newItem
      }
    }
  }
}