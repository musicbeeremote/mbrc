package com.kelsos.mbrc.features.library.presentation.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.MenuItemSelectedListener
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.popup
import com.kelsos.mbrc.features.library.presentation.viewholders.ArtistViewHolder

class ArtistAdapter : PagingDataAdapter<Artist, ArtistViewHolder>(DIFF_CALLBACK) {
  private var listener: MenuItemSelectedListener<Artist>? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
    val holder = ArtistViewHolder.create(parent)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_artist) { id ->
        val artist = getItem(position) ?: return@popup
        listener?.onMenuItemSelected(id, artist)
      }
    }

    holder.onPress { position ->
      val artist = getItem(position) ?: return@onPress
      listener?.onItemClicked(artist)
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

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener<Artist>) {
    this.listener = listener
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
