package com.kelsos.mbrc.features.library.artists

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.queue.LibraryPopup
import com.kelsos.mbrc.ui.FastScrollableAdapter
import com.kelsos.mbrc.features.library.popup

class ArtistAdapter : FastScrollableAdapter<Artist, ArtistViewHolder>(DIFF_CALLBACK) {

  private val indicatorPressed: (View, Int) -> Unit = { view, position ->
    view.popup(R.menu.popup_artist) {

      val action = when (it) {
        R.id.popup_artist_album -> LibraryPopup.PROFILE
        R.id.popup_artist_queue_next -> LibraryPopup.NEXT
        R.id.popup_artist_queue_last -> LibraryPopup.LAST
        R.id.popup_artist_play -> LibraryPopup.NOW
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

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
    return ArtistViewHolder.create(parent, indicatorPressed, pressed)
  }

  override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
    if (fastScrolling) {
      holder.clear()
      return
    }

    val artistEntity = getItem(position)
    if (artistEntity != null) {
      holder.bindTo(artistEntity)
    } else {
      holder.clear()
    }
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Artist>() {
      override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
        return oldItem == newItem
      }
    }
  }
}