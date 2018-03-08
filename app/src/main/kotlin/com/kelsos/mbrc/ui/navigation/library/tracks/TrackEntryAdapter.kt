package com.kelsos.mbrc.ui.navigation.library.tracks

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.ui.navigation.library.MenuItemSelectedListener
import com.kelsos.mbrc.ui.navigation.library.popup
import javax.inject.Inject

class TrackEntryAdapter
@Inject
constructor() : PagingDataAdapter<Track, TrackViewHolder>(DIFF_CALLBACK) {
  private var listener: MenuItemSelectedListener<Track>? = null
  private var coverMode: Boolean = false

  fun setCoverMode(coverMode: Boolean) {
    this.coverMode = coverMode
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
    val holder = TrackViewHolder.create(parent, coverMode)
    holder.onIndicatorClick { view, position ->
      view.popup(R.menu.popup_track) { id ->
        val track = getItem(position) ?: return@popup
        listener?.onMenuItemSelected(id, track)
      }
    }

    holder.onPress { position ->
      val track = getItem(position) ?: return@onPress
      listener?.onItemClicked(track)
    }
    return holder
  }

  override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
    val track = getItem(position)
    if (track != null) {
      holder.bindTo(track)
    } else {
      holder.clear()
    }
  }

  fun setMenuItemSelectedListener(listener: MenuItemSelectedListener<Track>) {
    this.listener = listener
  }

  companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Track>() {
      override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.id == newItem.id
      }

      override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
      }
    }
  }
}
