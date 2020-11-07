package com.kelsos.mbrc.features.library.presentation.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.popup
import com.kelsos.mbrc.features.library.presentation.viewholders.ArtistViewHolder

class ArtistAdapter : LibraryAdapter<Artist, ArtistViewHolder>(DIFF_CALLBACK) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
    val holder = ArtistViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_artist) { id ->
        val artist = getItem(position) ?: return@popup
        requireListener().onMenuItemSelected(id, artist)
      }
    }

    holder.onPress { position ->
      val artist = getItem(position) ?: return@onPress
      requireListener().onItemClicked(artist)
    }
    return holder
  }

  override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
    val artist = getItem(position)
    if (artist != null) {
      holder.bindTo(artist)
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
