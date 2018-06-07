package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.recyclerview.widget.DiffUtil
import android.view.View
import android.view.ViewGroup
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.ui.FastScrollableAdapter
import com.kelsos.mbrc.ui.navigation.library.popup
import javax.inject.Inject

class AlbumEntryAdapter
@Inject
constructor() : FastScrollableAdapter<AlbumEntity, AlbumViewHolder>(DIFF_CALLBACK) {

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

    val albumEntity = getItem(position)

    if (albumEntity != null) {
      holder.bindTo(albumEntity)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AlbumEntity>() {
      override fun areItemsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: AlbumEntity, newItem: AlbumEntity): Boolean {
        return oldItem == newItem
      }
    }
  }
}