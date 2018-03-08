package com.kelsos.mbrc.ui.navigation.library.artists

import android.support.v7.util.DiffUtil
import android.view.View
import android.view.ViewGroup
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.ui.FastScrollableAdapter
import com.kelsos.mbrc.ui.navigation.library.popup
import javax.inject.Inject

class ArtistEntryAdapter
@Inject
constructor() : FastScrollableAdapter<ArtistEntity, ArtistViewHolder>(DIFF_CALLBACK) {

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
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArtistEntity>() {
      override fun areItemsTheSame(oldItem: ArtistEntity, newItem: ArtistEntity): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: ArtistEntity, newItem: ArtistEntity): Boolean {
        return oldItem == newItem
      }
    }
  }
}